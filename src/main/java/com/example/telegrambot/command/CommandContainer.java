package com.example.telegrambot.command;

import com.example.telegrambot.command.AdminCommand.AddFactCommand;
import com.example.telegrambot.command.AutoShow.AddShowCommand;
import com.example.telegrambot.command.UserCommand.CallBackMoreInfoCommand;
import com.example.telegrambot.command.UserCommand.ExhibitionsNowCommand;
import com.example.telegrambot.command.UserCommand.InterestingFacts;
import com.example.telegrambot.command.UserCommand.MoreInfoCommand;
import com.example.telegrambot.model.*;
import com.example.telegrambot.service.IsAdmin;
import com.example.telegrambot.service.SendBotMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import static com.example.telegrambot.command.CommandName.*;


@Component
public class CommandContainer {

    private Command unknownCommand;

    @Autowired
    private IsAdmin isAdmin;

    private AutoShowsRepository autoShowsRepository;

    private SendBotMessageService sendBotMessageService;

    private HashMap<String, Command> adminCommandMap = new HashMap<>();
    private HashMap<String, Command> adminCommandMapWithCallback = new HashMap<>();
    private HashMap<String, Command> userCommandMap = new HashMap<>();
    private HashMap<String, Command> userCommandMapWithCallback = new HashMap<>();
    private HashMap<Boolean, String> waitCommand = new HashMap<>();

    final static boolean isWaitCommand = true;

    public void fillMap(SendBotMessageService sendBotMessageService, UserRepository userRepository,
                        AutoShowsRepository autoShowsRepository, FactsRepository factsRepository){

        adminCommandMap.put(START.getCommandName(),new StartCommand(sendBotMessageService, userRepository));
        adminCommandMap.put(HELP.getCommandName(), new HelpCommand(sendBotMessageService));
        adminCommandMapWithCallback.put(SEND.getCommandName(), new SendCommand(sendBotMessageService, userRepository));
        adminCommandMapWithCallback.put(ADDSHOW.getCommandName(), new AddShowCommand(sendBotMessageService,autoShowsRepository));
        adminCommandMapWithCallback.put(ADDFACT.getCommandName(), new AddFactCommand(sendBotMessageService, factsRepository));

        userCommandMap.put(START.getCommandName(),new StartCommand(sendBotMessageService, userRepository));
        userCommandMap.put(HELP.getCommandName(), new HelpCommand(sendBotMessageService));
        userCommandMap.put(EXHIBITIONSNOW.getCommandName(),new ExhibitionsNowCommand(sendBotMessageService, autoShowsRepository));
        userCommandMap.put(FACT.getCommandName(), new InterestingFacts(sendBotMessageService, factsRepository));
        userCommandMapWithCallback.put(MOREINFO.getCommandName(), new MoreInfoCommand(sendBotMessageService, autoShowsRepository));

        unknownCommand = new UnknownCommand(sendBotMessageService);

        this.autoShowsRepository = autoShowsRepository;
        this.sendBotMessageService = sendBotMessageService;
    }

    public Command findCommand(long userId, String command) {

        Command commandOrDefault;

        commandOrDefault = userCommandMap.getOrDefault(command, unknownCommand);

        if(isAdmin.checkAdmin(userId)){
            if(adminCommandMap.containsKey(command)){
                waitCommand.put(isWaitCommand, command);
                sendBotMessageService.messageToCallBack(userId, command);
            }
            else if(!waitCommand.isEmpty()){
                String commandFromMap = waitCommand.get(isWaitCommand);
                commandOrDefault = adminCommandMap.getOrDefault(commandFromMap, unknownCommand);
                waitCommand.clear();
            }
        }

        /*if(isAdmin.checkAdmin(userId)){
            commandOrDefault = adminCommandMap.getOrDefault(command, unknownCommand);
        } else {
            commandOrDefault = userCommandMap.getOrDefault(command, unknownCommand);
        }*/

        return commandOrDefault;
    }

    public Command findShowCommand(String callBackCommand, HashMap<String, Long> showDescription){
        Command showCommand;

        if(showDescription.containsKey(callBackCommand)){
            long showCommandId = showDescription.get(callBackCommand);
            Optional<AutoShows> autoShow = autoShowsRepository.findById(showCommandId);
            AutoShows autoShows = autoShow.get();
            showCommand = new CallBackMoreInfoCommand(sendBotMessageService, autoShowsRepository, autoShows);
        } else {
            showCommand = null;
        }


        return showCommand;
    }

    public boolean isCommandExist(long userId, String callBackCommand) {

        boolean command = false;

        if(isAdmin.checkAdmin(userId)){
            command = adminCommandMap.containsKey(callBackCommand);
        } else {
            command = userCommandMap.containsKey(callBackCommand);
        }
        return command;
    }


}

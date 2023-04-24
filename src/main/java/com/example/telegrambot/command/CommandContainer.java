package com.example.telegrambot.command;

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

    private HashMap<String, Command> adminCommandMap = new HashMap();
    private HashMap<String, Command> userCommandMap = new HashMap();

    public void fillMap(SendBotMessageService sendBotMessageService, UserRepository userRepository, AutoShowsRepository autoShowsRepository, FactsRepository factsRepository){

        adminCommandMap.put(START.getCommandName(),new StartCommand(sendBotMessageService, userRepository));
        adminCommandMap.put(HELP.getCommandName(), new HelpCommand(sendBotMessageService));
        adminCommandMap.put(SEND.getCommandName(), new SendCommand(sendBotMessageService, userRepository));
        adminCommandMap.put(ADDSHOW.getCommandName(), new AddShowCommand(sendBotMessageService,autoShowsRepository));

        userCommandMap.put(START.getCommandName(),new StartCommand(sendBotMessageService, userRepository));
        userCommandMap.put(HELP.getCommandName(), new HelpCommand(sendBotMessageService));
        userCommandMap.put(EXHIBITIONSNOW.getCommandName(),new ExhibitionsNowCommand(sendBotMessageService, autoShowsRepository));
        userCommandMap.put(FACT.getCommandName(), new InterestingFacts(sendBotMessageService, factsRepository));
        userCommandMap.put(MOREINFO.getCommandName(), new MoreInfoCommand(sendBotMessageService, autoShowsRepository));

        unknownCommand = new UnknownCommand(sendBotMessageService);

        this.autoShowsRepository = autoShowsRepository;
        this.sendBotMessageService =sendBotMessageService;
    }

    public Command findCommand(String chatUserName, String commandIdentifier) {

        Command commandOrDefault;

        if(isAdmin.checkAdmin(chatUserName)){
            commandOrDefault = adminCommandMap.getOrDefault(commandIdentifier, unknownCommand);
        } else {
            commandOrDefault = userCommandMap.getOrDefault(commandIdentifier, unknownCommand);
        }

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

    public boolean findCallBackCommand(String chatUserName, String callBackCommand) {

        boolean command = false;

        if(isAdmin.checkAdmin(chatUserName)){
            command = adminCommandMap.containsKey(callBackCommand);
        } else {
            command = userCommandMap.containsKey(callBackCommand);
        }
        return command;
    }


}

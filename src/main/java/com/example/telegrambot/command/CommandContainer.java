package com.example.telegrambot.command;

import com.example.telegrambot.command.AutoShow.AddShowCommand;
import com.example.telegrambot.model.AutoShowsRepository;
import com.example.telegrambot.model.UserRepository;
import com.example.telegrambot.service.IsAdmin;
import com.example.telegrambot.service.SendBotMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;

import static com.example.telegrambot.command.CommandName.*;


@Component
public class CommandContainer {


    private Command unknownCommand;

    @Autowired
    private IsAdmin isAdmin;

    private HashMap<String, Command> adminCommandMap = new HashMap();
    private HashMap<String, Command> userCommandMap = new HashMap();


    /*public CommandContainer(SendBotMessageService sendBotMessageService, UserRepository userRepository) {
        commandMap.put(START.getCommandName(), new StartAndRegistration(sendBotMessageService, userRepository));

        unknownCommand = new UnknownCommand();
    }*/

    public void fillMap(SendBotMessageService sendBotMessageService, UserRepository userRepository, AutoShowsRepository autoShowsRepository){

        adminCommandMap.put(START.getCommandName(),new StartCommand(sendBotMessageService, userRepository));
        adminCommandMap.put(HELP.getCommandName(), new HelpCommand(sendBotMessageService));
        adminCommandMap.put(SEND.getCommandName(), new SendCommand(sendBotMessageService, userRepository));
        adminCommandMap.put(ADDSHOW.getCommandName(), new AddShowCommand(sendBotMessageService,autoShowsRepository));

        unknownCommand = new UnknownCommand(sendBotMessageService);

        //callBackCommandMap.put(ADDSHOW.getCommandName(),new AddShowCallBackCommand());
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


    public boolean findCalBackCommand(String chatUserName, String callBackCommand) {

        boolean command = false;

        if(isAdmin.checkAdmin(chatUserName)){
            command = adminCommandMap.containsKey(callBackCommand);
        } else {
            command = userCommandMap.containsKey(callBackCommand);
        }
        return command;
    }

}

package com.example.telegrambot.command;

import com.example.telegrambot.model.AutoShowsRepository;
import com.example.telegrambot.model.UserRepository;
import com.example.telegrambot.service.SendBotMessageService;
import org.springframework.stereotype.Component;

import java.util.HashMap;

import static com.example.telegrambot.command.CommandName.*;


@Component
public class CommandContainer {


    private Command unknownCommand;


    private HashMap<String, Command> commandMap = new HashMap();


    /*public CommandContainer(SendBotMessageService sendBotMessageService, UserRepository userRepository) {
        commandMap.put(START.getCommandName(), new StartAndRegistration(sendBotMessageService, userRepository));

        unknownCommand = new UnknownCommand();
    }*/

    public void fillMap(SendBotMessageService sendBotMessageService, UserRepository userRepository, AutoShowsRepository autoShowsRepository){

        commandMap.put(START.getCommandName(),new StartCommand(sendBotMessageService, userRepository));
        commandMap.put(HELP.getCommandName(), new HelpCommand(sendBotMessageService));
        commandMap.put(SEND.getCommandName(), new SendCommand(sendBotMessageService, userRepository));
        commandMap.put(ADDSHOW.getCommandName(), new AddShowCommand(sendBotMessageService,autoShowsRepository));

        unknownCommand = new UnknownCommand(sendBotMessageService);
    }

    public Command findCommand(String commandIdentifier) {

        Command commandOrDefault = commandMap.getOrDefault(commandIdentifier, unknownCommand);
        /*if (isAdmin.checkAdmin(chatUserName)) {
            if (admins.contains(username)) {
                return orDefault;
            } else {
                return unknownCommand;
            }
        }*/
        return commandOrDefault;
    }
}

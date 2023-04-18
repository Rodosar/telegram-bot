package com.example.telegrambot.command;

import com.example.telegrambot.model.UserRepository;
import com.example.telegrambot.service.IsAdmin;
import com.example.telegrambot.service.SendBotMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

import static com.example.telegrambot.command.CommandName.*;


@Component
public class CommandContainer {


    private Command unknownCommand;


    private HashMap<String, Command> commandMap = new HashMap();


    /*public CommandContainer(SendBotMessageService sendBotMessageService) {
        this.commandMap.put(START.getCommandName(), new StartAndRegistration(sendBotMessageService));

        unknownCommand = new UnknownCommand(sendBotMessageService);
    }*/

    public void fillMap(SendBotMessageService sendBotMessageService, UserRepository userRepository){
        commandMap.put(START.getCommandName(),new StartAndRegistration(sendBotMessageService, userRepository));

        unknownCommand = new UnknownCommand();
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

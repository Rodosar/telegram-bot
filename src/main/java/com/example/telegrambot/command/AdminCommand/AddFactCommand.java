package com.example.telegrambot.command.AdminCommand;

import com.example.telegrambot.command.Command;
import com.example.telegrambot.model.Facts;
import com.example.telegrambot.model.FactsRepository;
import com.example.telegrambot.service.SendBotMessageService;
import org.telegram.telegrambots.meta.api.objects.Update;

public class AddFactCommand implements Command {


    private SendBotMessageService sendBotMessageService;

    private FactsRepository factsRepository;

    private Facts facts;

    public AddFactCommand(SendBotMessageService sendBotMessageService, FactsRepository factsRepository){
        this.sendBotMessageService = sendBotMessageService;
        this.factsRepository = factsRepository;
        this.facts = new Facts();
    }

    @Override
    public void execute(Update update) {

        long chatId = update.getMessage().getChatId();
        String commandRegexWithSpace = "^\\/[a-z]+\\s*";
        String textFromMessage = (update.getMessage().getText()).replaceAll(commandRegexWithSpace, "");

        String[] splitMessage = textFromMessage.split("\n");

        for(int i = 0; i<splitMessage.length; i++){
            splitMessage[i] = splitMessage[i].replaceAll("^\\s*", "");
        }

        facts.setTitle(splitMessage[0]);
        facts.setDescription(splitMessage[1]);
        facts.setFact("0");

        factsRepository.save(facts);
        sendBotMessageService.prepareAndSendMessage(chatId,"Факт добавлен!");
    }


}

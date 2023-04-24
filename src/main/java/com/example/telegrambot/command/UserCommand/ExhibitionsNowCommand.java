package com.example.telegrambot.command.UserCommand;

import com.example.telegrambot.command.Command;
import com.example.telegrambot.model.AutoShows;
import com.example.telegrambot.model.AutoShowsRepository;
import com.example.telegrambot.service.SendBotMessageService;
import org.telegram.telegrambots.meta.api.objects.Update;

public class ExhibitionsNowCommand implements Command{

    private SendBotMessageService sendBotMessageService;

    private AutoShowsRepository autoShowsRepository;

    public ExhibitionsNowCommand(SendBotMessageService sendBotMessageService, AutoShowsRepository autoShowsRepository){
        this.sendBotMessageService = sendBotMessageService;
        this.autoShowsRepository = autoShowsRepository;
    }

    @Override
    public void execute(Update update) {
        long chatId = update.getMessage().getChatId();
        StringBuilder autoShowsMessage = new StringBuilder();
        Iterable<AutoShows> autoShows = autoShowsRepository.findAll();
        for(AutoShows autoShow : autoShows){
            autoShowsMessage.append(autoShow.toStringAllShows());
        }
        String message = String.valueOf(autoShowsMessage);
        sendBotMessageService.prepareAndSendMessage(chatId, message);
    }
}

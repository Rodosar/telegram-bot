package com.example.telegrambot.command.UserCommand;

import com.example.telegrambot.command.Command;
import com.example.telegrambot.model.AutoShows;
import com.example.telegrambot.model.AutoShowsRepository;
import com.example.telegrambot.service.SendBotMessageService;
import org.telegram.telegrambots.meta.api.objects.Update;

public class CallBackMoreInfoCommand implements Command {

    private SendBotMessageService sendBotMessageService;

    private AutoShowsRepository autoShowsRepository;

    private AutoShows autoShows;

    public CallBackMoreInfoCommand(SendBotMessageService sendBotMessageService, AutoShowsRepository autoShowsRepository, AutoShows autoShows){
        this.sendBotMessageService = sendBotMessageService;
        this.autoShowsRepository = autoShowsRepository;
        this.autoShows = autoShows;
    }

    @Override
    public void execute(Update update) {
        long chatId = update.getCallbackQuery().getMessage().getChatId();

        String textToSend = autoShows.toStringShow();
        sendBotMessageService.prepareAndSendMessage(chatId, textToSend);
    }
}

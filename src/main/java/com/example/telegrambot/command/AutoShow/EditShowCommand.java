package com.example.telegrambot.command.AutoShow;

import com.example.telegrambot.command.Command;
import com.example.telegrambot.model.AutoShows;
import com.example.telegrambot.model.AutoShowsRepository;
import com.example.telegrambot.service.SendBotMessageService;
import org.telegram.telegrambots.meta.api.objects.Update;

public class EditShowCommand implements Command {

    private SendBotMessageService sendBotMessageService;

    private AutoShowsRepository autoShowsRepository;

    private AutoShows autoShows;

    public EditShowCommand(SendBotMessageService sendBotMessageService, AutoShowsRepository autoShowsRepository){
        this.sendBotMessageService = sendBotMessageService;
        this.autoShowsRepository = autoShowsRepository;
        this.autoShows = new AutoShows();
    }

    @Override
    public void execute(Update update) {


        String commandRegexWithSpace = "^\\/[a-z]+\\s*";

        String textFromMessage= (update.getMessage().getText()).replaceAll(commandRegexWithSpace, "");
        //String autoShowName = textFromMessage.replaceAll("")



    }
}

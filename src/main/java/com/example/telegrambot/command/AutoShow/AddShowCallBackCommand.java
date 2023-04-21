package com.example.telegrambot.command.AutoShow;

import com.example.telegrambot.command.CommandCallBack;
import com.example.telegrambot.model.AutoShows;
import com.example.telegrambot.model.AutoShowsRepository;
import com.example.telegrambot.service.SendBotMessageService;

public class AddShowCallBackCommand implements CommandCallBack {

    private SendBotMessageService sendBotMessageService;

    private AutoShowsRepository autoShowsRepository;

    private AutoShows autoShows;

    public AddShowCallBackCommand(SendBotMessageService sendBotMessageService, AutoShowsRepository autoShowsRepository){
        this.sendBotMessageService = sendBotMessageService;
        this.autoShowsRepository = autoShowsRepository;
        this.autoShows = new AutoShows();
    }

    @Override
    public void executeCallBack(String callBackCommand) {

    }
}

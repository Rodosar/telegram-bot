package com.example.telegrambot.command;

import com.example.telegrambot.model.AdsRepository;
import com.example.telegrambot.service.SendBotMessageService;

public class AddAdsCommand {

    private AdsRepository adsRepository;

    private SendBotMessageService sendBotMessageService;

    public AddAdsCommand(SendBotMessageService sendBotMessageService, AdsRepository adsRepository){
        
    }
}

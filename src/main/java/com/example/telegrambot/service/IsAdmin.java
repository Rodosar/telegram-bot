package com.example.telegrambot.service;

import com.example.telegrambot.config.BotConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class IsAdmin {

    @Autowired
    private BotConfig config;

    public Boolean checkAdmin(String chatUserName){

        if(chatUserName.equals(config.getAdminUserName())){
            return true;
        }
        return false;
    }
}

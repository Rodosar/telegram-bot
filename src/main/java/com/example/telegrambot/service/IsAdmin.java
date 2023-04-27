package com.example.telegrambot.service;

import com.example.telegrambot.config.BotConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class IsAdmin {

    @Autowired
    private BotConfig config;

    public Boolean checkAdmin(long userId){

        if(userId == config.getAdminId()){
            return true;
        }
        return false;
    }
}

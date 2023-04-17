package com.example.telegrambot.methods;

import com.example.telegrambot.model.Ads;
import com.example.telegrambot.model.AdsRepository;
import com.vdurmont.emoji.EmojiParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class AdsMethods {

    @Autowired
    private AdsRepository adsRepository;

    public String addAds(String messageText){

        var textToSend = EmojiParser.parseToUnicode(messageText.substring(messageText.indexOf(" ")));
        if(textToSend.contains("")){
            return "Название не должно быть пустым!";
        }
        else {
            Ads ads = new Ads();
            ads.setAd(textToSend);
            adsRepository.save(ads);
            return "Автовыставка добавлена!";
        }
    }


    public String editAds(String messageText){

        Ads ads = new Ads();
        Long adsId = Long.valueOf(messageText.substring(9,10)); //ПЕРЕДЕЛАТЬ

        if(adsRepository.findById(adsId).isEmpty()){
            return "Автовыставки с таким ID не существует!";
        }

            var textToSend = EmojiParser.parseToUnicode(messageText.substring(messageText.lastIndexOf(" "))); //ПЕРЕДЕЛАТЬ
            ads.setId(adsId);
            ads.setAd(textToSend);
            adsRepository.save(ads);
            return "Автовыставка обновлена!";
    }
}


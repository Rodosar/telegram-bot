package com.example.telegrambot.service;

import com.example.telegrambot.bot.TelegramBot;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
public class SendBotMessageServiceImpl implements SendBotMessageService{

    @Autowired
    private IsAdmin isAdmin;

    @Autowired
    TelegramBot telegramBot;

    @Override
    public void prepareAndSendMessage(long chatId, String textToSend) {
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(textToSend);

        try {
            telegramBot.execute(message);
        } catch (TelegramApiException e) {
            log.error("Error execute message: " + e.getMessage());
        }
    }

    public void sendMessageWithKeyboard(long chatId, String chatUserName, String textToSend) {  //отправка сообщения при старте с клавиатурой
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(textToSend);

        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        List<KeyboardRow> keyboardRows = new ArrayList<>();
        KeyboardRow row = new KeyboardRow();

        if(isAdmin.checkAdmin(chatUserName)){
            KeyboardRow row2 = new KeyboardRow();
            row.add("Добавить автовыставку");
            row.add("Редактировать автовыставку");
            row.add("Отправить сообщение пользователям");
            row2.add("Какие сейчас проводятся автомобильные выставки?");
            row2.add("Расскажи какой-нибудь факт!");

            keyboardRows.add(row);
            keyboardRows.add(row2);
        }
        else {
            row.add("Какие сейчас проводятся автомобильные выставки?");
            row.add("Расскажи какой-нибудь факт!");
            keyboardRows.add(row);
        }

        replyKeyboardMarkup.setKeyboard(keyboardRows);
        message.setReplyMarkup(replyKeyboardMarkup);

        try {
            telegramBot.execute(message);
        } catch (TelegramApiException e) {
            log.error("Error execute message: " + e.getMessage());
        }
    }
}

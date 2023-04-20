package com.example.telegrambot.service;

import com.example.telegrambot.bot.TelegramBot;
import com.vdurmont.emoji.EmojiParser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
public class SendBotMessageServiceImpl implements SendBotMessageService{

    @Autowired
    private IsAdmin isAdmin;

    @Autowired
    private TelegramBot telegramBot;

    final static String PHOTO = "src/main/resources/images/help.jpg";

    final static String HELP_TEXT = "Этот бот предназначен для поиска автомобильных выставок, просмотра информации по этим выставкам и другой полезной информации";

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

    @Override
    public void sendPhoto(long chatId) {

        SendPhoto sendPhoto = new SendPhoto();
        sendPhoto.setChatId(String.valueOf(chatId));
        sendPhoto.setPhoto(new InputFile(new File(PHOTO)));

        try {
            telegramBot.execute(sendPhoto);
        } catch (TelegramApiException e) {
            log.error("Error execute message: " + e.getMessage());
        }
    }

    @Override
    public void sendPhotoWithText(long chatId, String text) {

        SendPhoto sendPhoto = new SendPhoto();
        sendPhoto.setChatId(String.valueOf(chatId));
        sendPhoto.setPhoto(new InputFile(new File(PHOTO)));
        String textToSend = EmojiParser.parseToUnicode(text);
        sendPhoto.setCaption(textToSend);

        try {
            telegramBot.execute(sendPhoto);
        } catch (TelegramApiException e) {
            log.error("Error send photo: " + e.getMessage());
        }
    }
}

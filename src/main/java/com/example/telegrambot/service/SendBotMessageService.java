package com.example.telegrambot.service;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;

public interface SendBotMessageService {

    void prepareAndSendMessage(long chatId, String textToSend);

    void sendMessageWithKeyboard(long chatId, String chatUserName, String message);

    void sendPhoto(long chatId);

    void sendPhotoWithText(long chatId, String text);

    void callBackSendMessage(long chatId, String text);
}

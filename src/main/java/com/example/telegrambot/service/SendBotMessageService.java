package com.example.telegrambot.service;

public interface SendBotMessageService {

    void prepareAndSendMessage(long chatId, String textToSend);

    void sendMessageWithKeyboard(long chatId, String chatUserName, String message);
}

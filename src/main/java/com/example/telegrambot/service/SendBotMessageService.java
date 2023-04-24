package com.example.telegrambot.service;

import com.example.telegrambot.model.AutoShowsRepository;

import java.util.HashMap;

public interface SendBotMessageService {

    void prepareAndSendMessage(long chatId, String textToSend);

    void sendMessageWithKeyboard(long chatId, String chatUserName, String message);

    void sendPhoto(long chatId);

    void sendPhotoWithText(long chatId, String text);

    void callBackSendMessage(long chatId, String text);

    void sendCallBackMessageButtonsShow(long chatId, AutoShowsRepository autoShowsRepository, String text);

    void sendCallBackMessageShow(long callBackChatId, String callBackCommand, HashMap<String, Long> showDescription);

    void messageToCallBack(long callBackChatId, String callBackCommand, String callBackText);

    void messageAboutShow(long chatId, long showId, String text);
}

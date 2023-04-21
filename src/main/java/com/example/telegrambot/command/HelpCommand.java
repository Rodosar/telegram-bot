package com.example.telegrambot.command;

import com.example.telegrambot.service.SendBotMessageService;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;


@Slf4j
public class HelpCommand implements Command{

    private SendBotMessageService sendBotMessageService;

    final static String PHOTO = "src/main/resources/images/help.jpg";

    final static String HELP_TEXT = "Этот бот предназначен для поиска автомобильных выставок, просмотра информации по этим выставкам и другой полезной информации";

    public HelpCommand(SendBotMessageService sendBotMessageService){
        this.sendBotMessageService = sendBotMessageService;
    }

    @Override
    public void execute(Update update) {

        long chatId = update.getMessage().getChatId();
        //sendBotMessageService.sendPhotoWithText(chatId, HELP_TEXT + ":blush:");
        sendBotMessageService.callBackSendMessage(chatId, HELP_TEXT);

    }


}

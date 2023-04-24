package com.example.telegrambot.service;

import com.example.telegrambot.bot.TelegramBot;
import com.example.telegrambot.command.CommandContainer;
import com.example.telegrambot.command.UserCommand.CallBackMoreInfoCommand;
import com.example.telegrambot.model.AutoShows;
import com.example.telegrambot.model.AutoShowsRepository;
import com.vdurmont.emoji.EmojiParser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@Component
@Slf4j
public class SendBotMessageServiceImpl implements SendBotMessageService{

    @Autowired
    private IsAdmin isAdmin;

    @Autowired
    private TelegramBot telegramBot;

    @Autowired
    private CommandContainer commandContainer;

    private List<String> showDescription;

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
        KeyboardRow row2 = new KeyboardRow();
        if(isAdmin.checkAdmin(chatUserName)){
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
            row2.add("Подробная информация про выставку");
            keyboardRows.add(row);
            keyboardRows.add(row2);
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

    @Override
    public void callBackSendMessage(long chatId, String text) {

        SendMessage sendMessage = new SendMessage();
        sendMessage.setText(text);
        sendMessage.setChatId(String.valueOf(chatId));

        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInLine = new ArrayList<>();
        List<InlineKeyboardButton> rowInLine = new ArrayList<>();

        InlineKeyboardButton editShow = new InlineKeyboardButton();
        editShow.setText("Отправить уведомление");
        editShow.setCallbackData("/send");

        InlineKeyboardButton addShow = new InlineKeyboardButton();
        addShow.setText("Добавить автовыставку");
        addShow.setCallbackData("/addshow");

        rowInLine.add(editShow);
        rowInLine.add(addShow);

        rowsInLine.add(rowInLine);

        inlineKeyboardMarkup.setKeyboard(rowsInLine);
        sendMessage.setReplyMarkup(inlineKeyboardMarkup);

        try {
            telegramBot.execute(sendMessage);
        } catch (TelegramApiException e) {
            log.error("Error send photo: " + e.getMessage());
        }
    }

    @Override
    public void sendCallBackMessageButtonsShow(long chatId, AutoShowsRepository autoShowsRepository, String text) {

        SendMessage sendMessage = new SendMessage();
        sendMessage.setText(text);
        sendMessage.setChatId(String.valueOf(chatId));

        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInLine = new ArrayList<>();

        Iterable<AutoShows> autoShows = autoShowsRepository.findAll();
        for(AutoShows show : autoShows){

            InlineKeyboardButton autoShowButton = new InlineKeyboardButton();
            autoShowButton.setText(show.getDescription());
            //autoShowButton.setCallbackData("/" + show.getDescription());
            autoShowButton.setCallbackData(show.getDescription());

            List<InlineKeyboardButton> rowInLine = new ArrayList<>();
            rowInLine.add(autoShowButton);
            rowsInLine.add(rowInLine);

        }

        inlineKeyboardMarkup.setKeyboard(rowsInLine);
        sendMessage.setReplyMarkup(inlineKeyboardMarkup);

        try {
            telegramBot.execute(sendMessage);
        } catch (TelegramApiException e) {
            log.error("Error send photo: " + e.getMessage());
        }
    }

    @Override
    public void sendCallBackMessageShow(long callBackChatId, String callBackCommand, HashMap<String, Long> showDescription){

        /*if(showDescription.containsKey(callBackCommand)){
            long showCommandId = showDescription.get(callBackCommand);
            Optional<AutoShows> autoShow = autoShowsRepository.findById(showCommandId);
            AutoShows autoShows = autoShow.get();
            showCommand = new CallBackMoreInfoCommand(sendBotMessageService, autoShowsRepository, autoShows);
        }*/
    }

    @Override
    public void messageToCallBack(long callBackChatId, String callBackCommand, String callBackText) {

        String textToSend = null;

        switch (callBackCommand){

            case ("/addshow"):
                textToSend = "Добавьте выставку!";
                break;

            case ("/send"):
                textToSend = "Введите уведомление для всех пользователей!";
                break;

            default:
                break;
        }

        SendMessage sendMessage = new SendMessage();
        sendMessage.setText(textToSend);
        sendMessage.setChatId(String.valueOf(callBackChatId));

        try {
            telegramBot.execute(sendMessage);
        } catch (TelegramApiException e) {
            log.error("Error send photo: " + e.getMessage());
        }

    }

    @Override
    public void messageAboutShow(long chatId, long showId, String text) {

    }
}

package com.example.telegrambot.service;

import com.example.telegrambot.config.BotConfig;
import com.example.telegrambot.model.Ads;
import com.example.telegrambot.model.AdsRepository;
import com.example.telegrambot.model.User;
import com.example.telegrambot.model.UserRepository;
import com.vdurmont.emoji.EmojiParser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.File;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
@PropertySource("classpath:images/")
public class TelegramBot extends TelegramLongPollingBot{

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AdsRepository adsRepository;

    final BotConfig config;

    final static String HELP_TEXT = "Этот бот предназначен для поиска автомобильных выставок, просмотра информации по этим выставкам и другой полезной информации";

    final static String PHOTO = "src/main/resources/images/help.jpg";

    final static String YES_BUTTON = "YES_BUTTON";

    final static String NO_BUTTON = "NO_BUTTON";


    public TelegramBot(BotConfig config){
        this.config=config;
        List<BotCommand> listOfCommands = new ArrayList<>();
        listOfCommands.add(new BotCommand("/start)", "Запуск бота"));
        listOfCommands.add(new BotCommand("/help)", "Инструкция по взаимодействию с ботом"));
        try {
            this.execute(new SetMyCommands(listOfCommands, new BotCommandScopeDefault(), null));
        }
        catch (TelegramApiException e){
            log.error("Error setting bots command list: " + e.getMessage());
        }
    }

    @Override
    public String getBotUsername() {
        return config.getBotName();
    }

    @Override
    public String getBotToken() {
        return config.getBotToken();
    }

    @Override
    public void onUpdateReceived(Update update) {

        if(update.hasMessage() && update.getMessage().hasText()){
            String messageText = update.getMessage().getText();
            long chatId = update.getMessage().getChatId();
            String name = update.getMessage().getChat().getFirstName();

            if (messageText.contains("/send") && chatId == config.getAdminId()){   //отправка сообщения всем пользователям
                var textToSend = EmojiParser.parseToUnicode(messageText.substring(messageText.indexOf(" ")));
                var users = userRepository.findAll();
                for (User user: users){
                    prepareAndSendMessage(user.getChatId(), textToSend);
                }
            }
            // TODO: доделать
            else if(messageText.contains("/addads") && chatId == config.getAdminId()){  //отправка в БД
                var textToSend = EmojiParser.parseToUnicode(messageText.substring(messageText.indexOf(" ")));
                Ads ads = new Ads();
                ads.setAd(textToSend);
                adsRepository.save(ads);
            }
            else {
                switch (messageText){

                    case "/start":
                        registerUser(update.getMessage());
                        startCommandReceived(chatId, name);
                        break;

                    case "/help":
                        sendPhoto(chatId,PHOTO);
                        break;

                    case "/test":
                        test(chatId);
                        break;

                    default: prepareAndSendMessage(chatId, "Sorry command was not recognized");
                }
            }
        }
        // TODO: НУЖНО ПЕРЕНЕСТИ И ПЕРЕДЕЛАТЬ
        else if (update.hasCallbackQuery()){
            String callBackData = update.getCallbackQuery().getData();
            long messageId = update.getCallbackQuery().getMessage().getMessageId();
            long chatId = update.getCallbackQuery().getMessage().getChatId();

            if(callBackData.equals(YES_BUTTON)){
                String text = "Вы нажали кнопку Да";
                executeEditMessageText(chatId, messageId, text);
            }
            else if(callBackData.equals(NO_BUTTON)){
                String text = "Вы нажали кнопку Нет";
                executeEditMessageText(chatId, messageId, text);
            }

        }
    }
    private void executeEditMessageText(long chatId, long messageId, String text){     //редактирование текста сообщения с его заменой
        EditMessageText message = new EditMessageText();
        message.setChatId(String.valueOf(chatId));
        message.setMessageId((int)messageId);
        message.setText(text);
        try {
            execute(message);
        } catch (TelegramApiException e) {
            log.error("Error edit text: " + e.getMessage());
        }
    }

    private void test(long chatId) {          //вывод кнопок под сообщением с последующим изменением самого сообщения
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText("Некоторый текст");

        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInLine = new ArrayList<>();
        List<InlineKeyboardButton> rowInLine = new ArrayList<>();

        InlineKeyboardButton yesButton = new InlineKeyboardButton();
        yesButton.setText("Да");
        yesButton.setCallbackData(YES_BUTTON);

        InlineKeyboardButton noButton = new InlineKeyboardButton();
        noButton.setText("Нет");
        noButton.setCallbackData(NO_BUTTON);

        rowInLine.add(yesButton);
        rowInLine.add(noButton);

        rowsInLine.add(rowInLine);

        inlineKeyboardMarkup.setKeyboard(rowsInLine);
        message.setReplyMarkup(inlineKeyboardMarkup);

        executeMessage(message);
    }

    private void registerUser(Message msg){                     //регистрация пользователя
        if(userRepository.findById(msg.getChatId()).isEmpty()){

            var chatId = msg.getChatId();
            var chat = msg.getChat();

            User user = new User();

            user.setChatId(chatId);
            user.setFirstName(chat.getFirstName());
            user.setLastName(chat.getLastName());
            user.setUserName(chat.getUserName());
            user.setRegisteredAt(new Timestamp(System.currentTimeMillis()));

            userRepository.save(user);
            log.info("User saved: " + user);
        }
    }

    /*private void addAds(Message msg){
        adsRepository.save(ad)
    }*/

    private void executeMessage (SendMessage message){
        try {
            execute(message);
        } catch (TelegramApiException e) {
            log.error("Error execute message: " + e.getMessage());
        }
    }
    private void startCommandReceived(Long chatId, String name){          //действия при нажатии /start
        String answer = "Hi " + name + "!";
        log.info("Replied to user " + name);
        sendMessage(chatId,answer);
    }

    private void sendMessage(long chatId, String textToSend){
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(textToSend);

        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        List<KeyboardRow> keyboardRows = new ArrayList<>();

        KeyboardRow row = new KeyboardRow();
        row.add("Какие сейчас проводятся автомобильные выставки?");
        row.add("Расскажи какой-нибудь факт!");
        keyboardRows.add(row);

        replyKeyboardMarkup.setKeyboard(keyboardRows);
        message.setReplyMarkup(replyKeyboardMarkup);
        executeMessage(message);
    }

    private void sendPhoto(long chatId, String photo){

        SendPhoto sendPhoto = new SendPhoto();
        sendPhoto.setChatId(String.valueOf(chatId));
        sendPhoto.setPhoto(new InputFile(new File(photo)));
        String emoji = EmojiParser.parseToUnicode(HELP_TEXT + ":blush:");  //добавление эмоджи
        sendPhoto.setCaption(emoji);

        try {
            execute(sendPhoto);
        } catch (TelegramApiException e) {
            log.error("Error send photo: " + e.getMessage());
        }
    }

    private void prepareAndSendMessage(long chatId, String textToSend){
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(textToSend);
        executeMessage(message);
    }

    /*@Scheduled(cron = "0 * * * * *")
    private void sendAds(){
        var ads = adsRepository.findAll();
        var users = userRepository.findAll();

        for (Ads ad: ads){
            for (User user: users){
                prepareAndSendMessage(user.getChatId(), ad.getAd());
            }
        }
    }*/


}

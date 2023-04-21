package com.example.telegrambot.bot;

import com.example.telegrambot.command.CommandContainer;
import com.example.telegrambot.config.BotConfig;
import com.example.telegrambot.methods.AdsMethods;
import com.example.telegrambot.model.*;
import com.example.telegrambot.service.SendBotMessageServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;
import java.util.HashMap;


@Slf4j
@Component
@PropertySource("classpath:images/")
public class TelegramBot extends TelegramLongPollingBot{

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AdsRepository adsRepository;

    @Autowired
    private AutoShowsRepository autoShowsRepository;

    @Autowired
    private AdsMethods adsMethods;

    @Autowired
    private CommandContainer commandContainer;

    @Lazy
    @Autowired
    private SendBotMessageServiceImpl sendBotMessageService;

    final BotConfig config;

    final static boolean isCallBack = true;

    private HashMap<Boolean, String> callBackMap;


    public TelegramBot(BotConfig config){
        this.config=config;
        callBackMap = new HashMap<>();
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

        commandContainer.fillMap(sendBotMessageService, userRepository, autoShowsRepository);

        if(update.hasCallbackQuery()){

            CallbackQuery callbackQuery = update.getCallbackQuery();
            String callBackCommand = callbackQuery.getData();
            String callBackChatUserName = callbackQuery.getMessage().getChat().getUserName();
            long callBackChatId = callbackQuery.getMessage().getChatId();

            callBackMap.put(isCallBack, callBackCommand);

            if(commandContainer.findCalBackCommand(callBackChatUserName, callBackCommand)){
                //TODO сделать для разных сообщений
                sendBotMessageService.messageToCallBack(callBackChatId, callBackCommand);
            }
        }
        else if(!callBackMap.isEmpty() && update.hasMessage() && update.getMessage().hasText()){

            String callBackCommand = callBackMap.get(isCallBack);
            String chatUserName = update.getMessage().getChat().getUserName();
            commandContainer.findCommand(chatUserName,callBackCommand).execute(update);
            callBackMap.clear();
        }
        else if(update.hasMessage() && update.getMessage().hasText()){

            String messageText = update.getMessage().getText();
            String chatUserName = update.getMessage().getChat().getUserName();

            if(messageText.startsWith("/")){
                String commandIdentifier = messageText.split(" ")[0].toLowerCase();
                commandContainer.findCommand(chatUserName, commandIdentifier).execute(update);
            }
        }
    }
}

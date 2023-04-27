package com.example.telegrambot.bot;

import com.example.telegrambot.command.CommandContainer;
import com.example.telegrambot.config.BotConfig;
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
    private FactsRepository factsRepository;

    @Autowired
    private CommandContainer commandContainer;

    @Lazy
    @Autowired
    private SendBotMessageServiceImpl sendBotMessageService;

    final BotConfig config;

    final static boolean isWaitCommand = true;

    private HashMap<Boolean, String> callBackMap;
    private HashMap<Boolean, String> waitCommand;
    private HashMap<String, Long> showDescription;

    private String showCommandName;

    public TelegramBot(BotConfig config){
        this.config=config;
        callBackMap = new HashMap<>();
        waitCommand = new HashMap<>();
        showDescription = new HashMap<>();


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

        commandContainer.fillMap(sendBotMessageService, userRepository, autoShowsRepository, factsRepository);
        Iterable<AutoShows> autoShow = autoShowsRepository.findAll();
        for(AutoShows show : autoShow){
            showDescription.put(show.getDescription(), show.getId());
        }

        if(update.hasCallbackQuery()){

            CallbackQuery callbackQuery = update.getCallbackQuery();
            String callBackCommand = callbackQuery.getData();
            showCommandName = callBackCommand;
            String callBackText = callbackQuery.getMessage().getText();
            String callBackChatUserName = callbackQuery.getMessage().getChat().getUserName();
            long callBackChatId = callbackQuery.getMessage().getChatId();

            callBackMap.put(isCallBack, callBackCommand);

            if(showDescription.containsKey(callBackCommand)){
                commandContainer.findShowCommand(callBackCommand, showDescription).execute(update);
            }
            if(commandContainer.findCallBackCommand(callBackChatUserName, callBackCommand)){
                //TODO сделать для разных сообщений
                sendBotMessageService.messageToCallBack(callBackChatId, callBackCommand, callBackText);
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
            } else if (commandContainer.findCallBackCommand(chatUserName, messageText)){
                commandContainer.findCommand(chatUserName, messageText).execute(update);
            }
        }
    }

        /*commandContainer.fillMap(sendBotMessageService, userRepository, autoShowsRepository, factsRepository);
        Iterable<AutoShows> autoShow = autoShowsRepository.findAll();
        for(AutoShows show : autoShow){
            showDescription.put(show.getDescription(), show.getId());
        }

        if(update.hasCallbackQuery()){
            CallbackQuery callbackQuery = update.getCallbackQuery();
            String callBackCommand = callbackQuery.getData();
            String callBackText = callbackQuery.getMessage().getText();
            String callBackChatUserName = callbackQuery.getMessage().getChat().getUserName();
            long callBackChatUserId = callbackQuery.getMessage().getChat().getId();
            long callBackChatId = callbackQuery.getMessage().getChatId();

            if(showDescription.containsKey(callBackCommand)){
                commandContainer.findShowCommand(callBackCommand, showDescription).execute(update);
            }
            else if(commandContainer.isCommandExist(callBackChatUserId, callBackCommand)){
                //TODO сделать для разных сообщений
                    sendBotMessageService.messageToCallBack(callBackChatId, callBackCommand);
                    callBackMap.put(isWaitCommand, callBackCommand);
                }
            }
        if (update.hasMessage() && update.getMessage().hasText()) {
            long chatUserId = update.getMessage().getChat().getId();
            String messageText = update.getMessage().getText();

            if (!callBackMap.isEmpty()) {
                String callBackCommand = callBackMap.get(isWaitCommand);
                commandContainer.findCommand(chatUserId, callBackCommand).execute(update);
                callBackMap.clear();
            }
            if (messageText.startsWith("/")) {
                String commandIdentifier = messageText.split(" ")[0].toLowerCase();
                commandContainer.findCommand(chatUserId, commandIdentifier).execute(update);
            } else if (commandContainer.isCommandExist(chatUserId, messageText)) {
                commandContainer.findCommand(chatUserId,messageText);
                sendBotMessageService.messageToCallBack(chatUserId, messageText);
                waitCommand.put(isWaitCommand, messageText);
            } else if(waitCommand.containsKey(isWaitCommand)){
                String waitCommandMessage = waitCommand.get(isWaitCommand);
                commandContainer.findCommand(chatUserId, waitCommandMessage).execute(update);
            }
        }*/




        /*if(update.hasCallbackQuery()){
            String callBackCommand = update.getCallbackQuery().getData();
            long callBackChatUserId = update.getCallbackQuery().getMessage().getChat().getId();

            if(commandContainer.isCommandExist(callBackChatUserId, callBackCommand)){
                commandContainer.findCommand(callBackChatUserId, callBackCommand);
            } else {
                sendBotMessageService.prepareAndSendMessage(callBackChatUserId, "Такой кнопки не существует!");
            }
        }
        else if(update.hasMessage() && update.getMessage().hasText()){
            String message = update.getMessage().getText();
            long chatId = update.getMessage().getChatId();
            long userId = update.getMessage().getChat().getId();

            if(commandContainer.isCommandExist(userId, message)){

                waitCommand.put(isWaitCommand, message);
                sendBotMessageService.messageToCallBack(userId, message);
            }
            else if(!waitCommand.isEmpty()){
                String commandFromMap = waitCommand.get(isWaitCommand);
                commandContainer.findCommand(userId, commandFromMap);
            } else {
                commandContainer.findCommand(userId, message);
            }
        }


        if(update.hasMessage() && update.getMessage().hasText()){
            long chatUserId = update.getMessage().getChat().getId();
            String messageText = update.getMessage().getText();

            if (messageText.startsWith("/")) {
                String commandIdentifier = messageText.split(" ")[0].toLowerCase();
                commandContainer.findCommand(chatUserId, commandIdentifier).execute(update);
            }
            else if (commandContainer.isCommandExist(chatUserId, messageText)) {

            }
            else if (commandContainer.isCommandExist(chatUserId, messageText.)){
                commandContainer.findCommand(chatUserId,messageText);
                sendBotMessageService.messageToCallBack(chatUserId, messageText);
                waitCommand.put(isWaitCommand, messageText);
            }
        }
        if(update.hasCallbackQuery()){
            String callBackCommand = update.getCallbackQuery().getData();
            long callBackChatUserId = update.getCallbackQuery().getMessage().getChat().getId();
            long callBackChatId = update.getCallbackQuery().getMessage().getChatId();

            commandContainer
        }*/
    }
}

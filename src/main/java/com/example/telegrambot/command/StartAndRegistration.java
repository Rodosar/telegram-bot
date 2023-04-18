package com.example.telegrambot.command;

import com.example.telegrambot.model.User;
import com.example.telegrambot.model.UserRepository;
import com.example.telegrambot.service.SendBotMessageService;
import com.example.telegrambot.service.SendBotMessageServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.sql.Timestamp;


@EnableJpaRepositories
@Slf4j
public class StartAndRegistration implements Command {


    private UserRepository userRepository;

    private SendBotMessageService sendBotMessageService;


    public StartAndRegistration(SendBotMessageService sendBotMessageService, UserRepository userRepository) {
        this.sendBotMessageService = sendBotMessageService;
        this.userRepository = userRepository;
    }

    @Override
    public void execute(Update update) {
        long chatId = update.getMessage().getChatId();
        String chatUserName = update.getMessage().getChat().getUserName();
        String firstName = update.getMessage().getChat().getFirstName();
        Message message = update.getMessage();

        registerUser(chatId, message);
        startCommandReceived(chatId, chatUserName, firstName);
    }

    private void startCommandReceived(long chatId, String chatUserName, String firstName){          //действия при нажатии /start
        String textToSend = "Hi " + firstName + "!";
        log.info("Replied to user " + firstName);

        sendBotMessageService.sendMessageWithKeyboard(chatId, chatUserName, textToSend);
    }

    private void registerUser(long chatId, Message message){                     //регистрация пользователя
        if(userRepository.findById(chatId).isEmpty()){

            Chat chat = message.getChat();

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
}

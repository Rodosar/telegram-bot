package com.example.telegrambot.command;

import com.example.telegrambot.model.User;
import com.example.telegrambot.model.UserRepository;
import com.example.telegrambot.service.SendBotMessageService;
import com.vdurmont.emoji.EmojiParser;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.objects.Update;

@Slf4j
public class SendCommand implements Command{

    private UserRepository userRepository;

    private SendBotMessageService sendBotMessageService;

    public SendCommand(SendBotMessageService sendBotMessageService, UserRepository userRepository){
        this.sendBotMessageService = sendBotMessageService;
        this.userRepository = userRepository;
    }

    @Override
    public void execute(Update update) {

        long chatId = update.getMessage().getChatId();


        String textFromMessage = EmojiParser.parseToUnicode(update.getMessage().getText());
        String commandRegExSpace = "^\\/[a-z]+\\s";
        String textToSend = textFromMessage.replaceAll(commandRegExSpace, "");

        Iterable<User> users= userRepository.findAll();
        for (User user : users) {
            sendBotMessageService.prepareAndSendMessage(user.getChatId(), textToSend);
        }
    }
}

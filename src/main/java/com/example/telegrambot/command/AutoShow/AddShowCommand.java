package com.example.telegrambot.command.AutoShow;

import com.example.telegrambot.command.Command;
import com.example.telegrambot.model.AutoShows;
import com.example.telegrambot.model.AutoShowsRepository;
import com.example.telegrambot.service.SendBotMessageService;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;


public class AddShowCommand implements Command {

    private SendBotMessageService sendBotMessageService;

    private AutoShowsRepository autoShowsRepository;

    private AutoShows autoShows;

    public AddShowCommand(SendBotMessageService sendBotMessageService, AutoShowsRepository autoShowsRepository){
        this.sendBotMessageService = sendBotMessageService;
        this.autoShowsRepository = autoShowsRepository;
        this.autoShows = new AutoShows();
    }

    @Override
    public void execute(Update update) {

        long chatId = update.getMessage().getChatId();

        String commandRegexWithSpace = "^\\/[a-z]+\\s*";
        String textFromMessage = (update.getMessage().getText()).replaceAll(commandRegexWithSpace, "");

        String[] splitMessage = textFromMessage.split("\n");

        for(int i = 0; i<splitMessage.length; i++){
            splitMessage[i] = splitMessage[i].replaceAll("^\\s*", "");
        }

        SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy HH:mm");
        Date date = null;
        try {
            date = format.parse(splitMessage[5]);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        autoShows.setDescription(splitMessage[0]);
        autoShows.setDetailedDescription(splitMessage[1]);
        autoShows.setAutoCompany(splitMessage[2]);
        autoShows.setMainCars(splitMessage[3]);
        autoShows.setCharacteristicsOfTheMainCars(splitMessage[4]);
        autoShows.setDateOfTheEvent(date);

        autoShowsRepository.save(autoShows);
        sendBotMessageService.prepareAndSendMessage(chatId,"Автовыставка добавлена!");
    }
}

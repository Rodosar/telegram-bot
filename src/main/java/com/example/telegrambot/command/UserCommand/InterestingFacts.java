package com.example.telegrambot.command.UserCommand;

import com.example.telegrambot.command.Command;
import com.example.telegrambot.command.CommandContainer;
import com.example.telegrambot.model.Facts;
import com.example.telegrambot.model.FactsRepository;
import com.example.telegrambot.service.SendBotMessageService;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.*;

//todo доделать чтобы всегда рандомные факты выдавались
@Component
public class InterestingFacts implements Command {

    SendBotMessageService sendBotMessageService;

    FactsRepository factsRepository;

    CommandContainer commandContainer;

    private Map<Long, Integer> factsMap;
    private ArrayList<Long> factsArray;
    private ArrayList<Long> removeFactsArray;

    public InterestingFacts(SendBotMessageService sendBotMessageService, FactsRepository factsRepository) {
        this.sendBotMessageService = sendBotMessageService;
        this.factsRepository = factsRepository;

        factsMap = new HashMap<>();
        factsArray = new ArrayList<>();
        Iterable<Facts> facts = factsRepository.findAll();
        for (Facts fact : facts) {
            long factId = fact.getId();
            int t = Integer.parseInt(fact.getFact());
            factsMap.put(factId, t);
            factsArray.add(factId);
        }
    }

    @Override
    public void execute(Update update) {

        long chatId = update.getMessage().getChatId();

        Random random = new Random();

        long factId = factsArray.get(random.nextInt(factsArray.size()));

        Optional<Facts> getFact = factsRepository.findById(factId);
        Facts fact = getFact.get();
        sendBotMessageService.prepareAndSendMessage(chatId, fact.toString());
       /* if (factsMap.get(factId) == 0){
            sendBotMessageService.prepareAndSendMessage(chatId, fact.toString());
            factsMap.computeIfPresent(factId, (a,b) -> b = 1);
            Facts facts = new Facts();
            facts.setFact();
            factsRepository.save();
        }

        int i = Integer.parseInt(fact.getFact());


        factsArray = new ArrayList();
        if (removeFactsArray == null){
            removeFactsArray = new ArrayList<>();
        }
        Iterable<Facts> facts = factsRepository.findAll();
        for (Facts fact : facts) {
            long factId = fact.getId();
            factsArray.add(factId);
        }
        if(removeFactsArray.isEmpty()){
            removeFactsArray = factsArray;
        }
*/

    }


}

package com.example.telegrambot.model;


import jdk.jfr.BooleanFlag;
import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Data
@Entity(name = "factsTable")
public class Facts {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    String title;

    String description;

    String fact;

    @Override
    public String toString() {
        return "Интересный факт!" + '\n' +
                title + '\n' +
                description;
    }
}

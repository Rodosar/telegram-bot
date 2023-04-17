package com.example.telegrambot.model;


import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.sql.Timestamp;

@Entity(name = "autoShowsTable")
public class AutoShows {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    String description;

    String detailedDescription;

    String autoCompany;

    String mainCars;

    String characteristicsOfTheMainCars;

    Timestamp dateOfTheEvent;
}

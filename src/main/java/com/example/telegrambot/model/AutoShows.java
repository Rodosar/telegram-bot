package com.example.telegrambot.model;


import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

@Data
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

    Date dateOfTheEvent;

    public String toStringAllShows() {
        return  "Описание: " + description + '\n' +
                "Автокомпании: " + autoCompany + '\n' +
                "Дата проведения: " + dateOfTheEvent + '\n' + '\n';
    }

    public String toStringShow() {
        return  "Описание: " + description + '\n' +
                "Подробное писание: " + detailedDescription + '\n' +
                "Автокомпании: " + autoCompany + '\n' +
                "Главные экспонаты: " + mainCars + '\n' +
                "Характеристики главных экспонатов: " + characteristicsOfTheMainCars + '\n' +
                "Дата проведения: " + dateOfTheEvent;
    }
}

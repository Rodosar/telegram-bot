package com.example.telegrambot.command;

public enum CommandName {


    START ("/start"),
    HELP("/help"),
    NO("nocommand"),
    SEND ("/send"),
    ADDADS("/addads"),
    EDITADS("/editads"),
    DELETEADS("/deleteads"),
    ADDSHOW("/addshow"),
    EDITSHOW("/editshow"),
    DELETESHOW("/deleteshow");


    private String commandName;

    CommandName(String commandName) {
        this.commandName = commandName;
    }

    public String getCommandName() {
        return commandName;
    }
}

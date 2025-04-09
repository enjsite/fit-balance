package ru.enjy.fit_balance.telegram.command;

public enum CommandName {

    START("/start"),
    REGISTRATION("/registration"),
    HELP("/help");

    private final String commandName;

    CommandName(String commandName) {
        this.commandName = commandName;
    }

    public String getCommandName() {
        return commandName;
    }
}

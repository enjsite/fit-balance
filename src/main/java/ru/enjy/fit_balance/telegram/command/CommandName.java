package ru.enjy.fit_balance.telegram.command;

import lombok.Getter;

@Getter
public enum CommandName {

    START("/start"),
    REGISTRATION("/registration"),
    START_WORKOUT("/start_workout"),
    SUPERSET("/superset"),
    SET("/set"),
    HELP("/help");

    private final String command;

    CommandName(String command) {
        this.command = command;
    }

    public static CommandName getByCommand(String command) {
        for (CommandName commandName : values()) {
            if (commandName.getCommand().equals(command)) {
                return commandName;
            }
        }
        return null;
    }
}

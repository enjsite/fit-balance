package ru.enjy.fit_balance.telegram.command;

import lombok.Getter;

@Getter
public enum CommandName {

    START("/start"),
    START_WORKOUT("/start_workout"),
    FINISH_WORKOUT("/finish_workout"),
    START_SUPERSET("/start_superset"),
    START_SINGLESET("/start_singleset"),
    FINISH_SUPERSET("/finish_superset"),
    ADD_SET("/add_set"),
    ADD_EXERCISE("/add_exercise"),
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

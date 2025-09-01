package ru.enjy.fit_balance.telegram.command;

public enum CommandName {

    START("/start"),
    REGISTRATION("/registration"),
    WORKOUT("/workout"),
    SUPERSET("/superset"),
    HELP("/help");

    private final String command;

    CommandName(String command) {
        this.command = command;
    }

    public String getCommand() {
        return command;
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

package ru.enjy.fit_balance.telegram.command;

import org.springframework.stereotype.Component;
import java.util.HashMap;

@Component
public class CommandContainer {

    private final HashMap<String, Command> commandMap;

    public CommandContainer() {
        commandMap = new HashMap<>();
    }

    public void setCommandMap(Command command) {
        commandMap.put(command.getCommandName(), command);
    }

    public Command getCommand(String commandIdentifier) {
        return commandMap.getOrDefault(commandIdentifier, null);
    }

}

package ru.enjy.fit_balance.telegram.command;

import org.springframework.stereotype.Component;
import ru.enjy.fit_balance.service.UserAccountService;
import ru.enjy.fit_balance.service.WorkoutService;
import ru.enjy.fit_balance.telegram.TelegramBotService;

import java.util.HashMap;

import static ru.enjy.fit_balance.telegram.command.CommandName.*;

@Component
public class CommandContainer {

    private final HashMap<String, Command> commandMap;

    private final Command unknownCommand;

    public CommandContainer(TelegramBotService telegramBotService,
                            UserAccountService userAccountService,
                            WorkoutService workoutService) {

        commandMap = new HashMap<>();
        commandMap.put(START.getCommandName(), new StartCommand(telegramBotService));
        commandMap.put(REGISTRATION.getCommandName(), new RegistrationCommand(telegramBotService, userAccountService));
        commandMap.put(WORKOUT.getCommandName(), new WorkoutCommand(telegramBotService, workoutService, userAccountService));
        commandMap.put(HELP.getCommandName(), new HelpCommand(telegramBotService));

        unknownCommand = new HelpCommand(telegramBotService);
    }

    public Command retrieveCommand(String commandIdentifier) {
        return commandMap.getOrDefault(commandIdentifier, unknownCommand);
    }

}

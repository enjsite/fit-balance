package ru.enjy.fit_balance.telegram.command;

import org.springframework.stereotype.Component;
import ru.enjy.fit_balance.telegram.bot.UpdateConsumer;

import static ru.enjy.fit_balance.telegram.command.CommandName.*;

@Component
public class HelpCommand implements Command {

    private final CommandName command = HELP;

    private final String HELP_MESSAGE = String.format("✨ Доcтупные команды ✨\n\n"

                    + "%s - начать работу со мной\n"
                    + "%s - получить помощь в работе со мной\n",
            START.getCommand(), HELP.getCommand());

    public HelpCommand(CommandContainer commandContainer) {
        commandContainer.setCommandMap(this);
    }

    @Override
    public String getCommandName() {
        return command.getCommand();
    }

    @Override
    public void execute(UpdateConsumer updateConsumer, Long chatId, Long exerciseId) {
        updateConsumer.sendMessage(chatId, HELP_MESSAGE);
    }
}

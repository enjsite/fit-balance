package ru.enjy.fit_balance.telegram.command;

import lombok.RequiredArgsConstructor;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.enjy.fit_balance.telegram.TelegramBotService;

import static ru.enjy.fit_balance.telegram.command.CommandName.*;

@RequiredArgsConstructor
public class HelpCommand implements Command {

    private final TelegramBotService telegramBotService;

    public static final String HELP_MESSAGE = String.format("✨ Доcтупные команды ✨\n\n"

                    + "%s - начать работу со мной\n"
                    + "%s - получить помощь в работе со мной\n",
            START.getCommandName(), HELP.getCommandName());

    @Override
    public void execute(Update update) {

        telegramBotService.sendTextMessageAsync(HELP_MESSAGE);
    }
}

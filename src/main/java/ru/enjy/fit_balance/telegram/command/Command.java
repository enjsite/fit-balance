package ru.enjy.fit_balance.telegram.command;

import ru.enjy.fit_balance.telegram.bot.UpdateConsumer;

public interface Command {

    void execute(UpdateConsumer updateConsumer, Long chatId, Long exerciseId);
    String getCommandName();
}

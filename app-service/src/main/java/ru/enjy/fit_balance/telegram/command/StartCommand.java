package ru.enjy.fit_balance.telegram.command;

import lombok.RequiredArgsConstructor;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.enjy.fit_balance.telegram.TelegramBotService;

import java.util.Map;

@RequiredArgsConstructor
public class StartCommand implements Command {

    private final TelegramBotService telegramBotService;

    @Override
    public void execute(Update update) {

        telegramBotService.sendTextMessageAsync("Привет! Хотите начать тренировку?", Map.of("Присоединиться!", "/registration"));
    }
}

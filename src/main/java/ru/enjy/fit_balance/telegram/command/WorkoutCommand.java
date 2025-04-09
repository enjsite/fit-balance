package ru.enjy.fit_balance.telegram.command;

import lombok.RequiredArgsConstructor;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.enjy.fit_balance.service.UserAccountService;
import ru.enjy.fit_balance.service.WorkoutService;
import ru.enjy.fit_balance.telegram.TelegramBotService;

import java.util.Map;

@RequiredArgsConstructor
public class WorkoutCommand implements Command {

    private final TelegramBotService telegramBotService;

    private final WorkoutService workoutService;
    private final UserAccountService userAccountService;

    @Override
    public void execute(Update update) {

        telegramBotService.sendTextMessageAsync("Выберите действие: ",
                Map.of("Начать суперсет", "/help",
                        "Выбрать упражнение", "/help",
                        "Закончить тренировку", "/help"));


    }
}

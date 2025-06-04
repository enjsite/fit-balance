package ru.enjy.fit_balance.telegram.command;

import lombok.RequiredArgsConstructor;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.enjy.fit_balance.service.UserAccountService;
import ru.enjy.fit_balance.telegram.TelegramBotService;

import java.util.Map;

@RequiredArgsConstructor
public class RegistrationCommand implements Command {

    private final TelegramBotService telegramBotService;

    private final UserAccountService userAccountService;

    @Override
    public void execute(Update update) {

        userAccountService.create(telegramBotService.getCurrentChatId().toString());
        telegramBotService.sendPhotoMessageAsync("trenirovka_nog_v_trenazhernom_zale.jpg");
        telegramBotService.sendTextMessageAsync("Вы успешно зарегистрированы!", Map.of("Начать тренировку", "/workout"));
    }
}

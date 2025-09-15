package ru.enjy.fit_balance.telegram.command;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;
import ru.enjy.fit_balance.model.dto.UserAccountDto;
import ru.enjy.fit_balance.service.UserAccountService;
import ru.enjy.fit_balance.service.WorkoutService;
import ru.enjy.fit_balance.telegram.bot.UpdateConsumer;

import java.util.List;

import static ru.enjy.fit_balance.telegram.command.CommandName.START_WORKOUT;

@Component
public class StartWorkoutCommand implements Command {

    private final CommandName command = START_WORKOUT;
    private final WorkoutService workoutService;
    private final UserAccountService userAccountService;

    public StartWorkoutCommand(CommandContainer commandContainer,
                               WorkoutService workoutService,
                               UserAccountService userAccountService) {
        commandContainer.setCommandMap(this);
        this.workoutService = workoutService;
        this.userAccountService = userAccountService;
    }

    @Override
    public void execute(UpdateConsumer updateConsumer, Long chatId, Long exerciseId) {

        UserAccountDto userAccountDto = userAccountService.findFirstByChatId(chatId.toString());
        var workout = workoutService.create(userAccountDto);

        var button1 = InlineKeyboardButton.builder()
                .text("Начать сет")
                .callbackData("/superset")
                .build();
        var button2 = InlineKeyboardButton.builder()
                .text("Закончить тренировку")
                .callbackData("/help")
                .build();
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup(
                List.of(
                        new InlineKeyboardRow(button1),
                        new InlineKeyboardRow(button2)
                ));
        updateConsumer.sendMessage(chatId, "Ваша тренировка: " + workout.getTitle());
        updateConsumer.sendMessageWithInlineKeyboard(chatId, markup, "Выберите действие:");
    }

    @Override
    public String getCommandName() {
        return command.getCommand();
    }
}

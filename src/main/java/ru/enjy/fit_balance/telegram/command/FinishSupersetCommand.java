package ru.enjy.fit_balance.telegram.command;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;
import ru.enjy.fit_balance.model.dto.SupersetDto;
import ru.enjy.fit_balance.model.dto.WorkoutDto;
import ru.enjy.fit_balance.service.ExerciseService;
import ru.enjy.fit_balance.service.SupersetService;
import ru.enjy.fit_balance.service.WorkoutService;
import ru.enjy.fit_balance.telegram.bot.UpdateConsumer;

import java.util.ArrayList;
import java.util.List;

import static ru.enjy.fit_balance.telegram.command.CommandName.*;

@Slf4j
@Component
public class FinishSupersetCommand implements Command {

    private final CommandName command = FINISH_SUPERSET;
    private WorkoutService workoutService;
    private SupersetService supersetService;

    public FinishSupersetCommand(CommandContainer commandContainer,
                                 WorkoutService workoutService,
                                 SupersetService supersetService) {
        commandContainer.setCommandMap(this);
        this.workoutService = workoutService;
        this.supersetService = supersetService;
    }

    @Override
    public void execute(UpdateConsumer updateConsumer, Long chatId, Long exerciseId) {

        WorkoutDto activeWorkout = workoutService.findFirstByActiveTrueAndUserChatId(chatId.toString());

        if (activeWorkout != null) {
            SupersetDto activeSuperset = supersetService.findFirstByActiveAndWorkout(activeWorkout);
            if (activeSuperset != null) {
                supersetService.finishSuperset(activeSuperset);
            }

            var button1 = InlineKeyboardButton.builder()
                    .text("Начать сет")
                    .callbackData(START_SUPERSET.getCommand())
                    .build();
            var button2 = InlineKeyboardButton.builder()
                    .text("Закончить тренировку")
                    .callbackData(FINISH_WORKOUT.getCommand())
                    .build();
            InlineKeyboardMarkup markup = new InlineKeyboardMarkup(
                    List.of(
                            new InlineKeyboardRow(button1),
                            new InlineKeyboardRow(button2)
                    ));
            updateConsumer.sendMessage(chatId, "Ваша тренировка: " + activeWorkout.getTitle());
            updateConsumer.sendMessageWithInlineKeyboard(chatId, markup, "Сет завершен. Выберите действие:");

        } else {
            var button = InlineKeyboardButton.builder()
                    .text("Начать тренировку")
                    .callbackData(START_WORKOUT.getCommand())
                    .build();
            InlineKeyboardMarkup markup = new InlineKeyboardMarkup(List.of(new InlineKeyboardRow(button)));
            updateConsumer.sendMessageWithInlineKeyboard(chatId, markup, "У вас нет активных тренировок. " +
                    "Если хотите продолжить, надо начать тренировку.");
        }
    }

    @Override
    public String getCommandName() {
        return command.getCommand();
    }
}

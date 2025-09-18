package ru.enjy.fit_balance.telegram.text;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;
import ru.enjy.fit_balance.model.dto.SetDto;
import ru.enjy.fit_balance.model.mapper.SetMapper;
import ru.enjy.fit_balance.service.ExerciseService;
import ru.enjy.fit_balance.service.SetService;
import ru.enjy.fit_balance.service.WorkoutService;
import ru.enjy.fit_balance.service.state.WorkoutInputState;
import ru.enjy.fit_balance.service.state.WorkoutStateService;
import ru.enjy.fit_balance.telegram.bot.UpdateConsumer;

import java.util.ArrayList;
import java.util.List;

import static ru.enjy.fit_balance.telegram.command.CommandName.*;


@Component
@RequiredArgsConstructor
@Slf4j
public class TextInputHandler {

    private final SetMapper setMapper;

    private final WorkoutStateService stateService;
    private final WorkoutService workoutService;
    private final ExerciseService exerciseService;
    private final SetService setService;

    public void handle(UpdateConsumer updateConsumer, Long chatId, String message) {

        WorkoutInputState state = stateService.getState(chatId.toString());
        log.info(state.toString());
        switch (state) {
            case WAITING_FOR_REPS -> processRepsInput(updateConsumer, message, chatId);
            case WAITING_FOR_WEIGHT -> processWeightInput(updateConsumer, message, chatId);
            default -> updateConsumer.sendMessage(chatId, "Неожиданный ввод: " + message);
        }
    }

    private void processRepsInput(UpdateConsumer updateConsumer, String input, Long chatId) {

        SetDto activeSet = setService.findFirstByActiveAndChatId(chatId.toString());
        try {
            int reps = Integer.parseInt(input);
            setService.saveReps(activeSet, reps);
            updateConsumer.sendMessage(chatId, "Сохранил " + reps + " повторов ✅");

            var exercises = exerciseService.getAll();
            List<InlineKeyboardRow> exercisesButtons = new ArrayList<>();
            exercises.forEach(ex -> {
                var button = InlineKeyboardButton.builder()
                        .text(ex.getTitle())
                        .callbackData("/ex" + ex.getId().toString())
                        .build();
                exercisesButtons.add(new InlineKeyboardRow(button));
            });

            var button1 = InlineKeyboardButton.builder()
                    .text("Закончить сет")
                    .callbackData(FINISH_SUPERSET.getCommand())
                    .build();
            var button2 = InlineKeyboardButton.builder()
                    .text("Закончить тренировку")
                    .callbackData(FINISH_WORKOUT.getCommand())
                    .build();
            exercisesButtons.add(new InlineKeyboardRow(button1));
            exercisesButtons.add(new InlineKeyboardRow(button2));
            InlineKeyboardMarkup markup = new InlineKeyboardMarkup(exercisesButtons);

            updateConsumer.sendMessageWithInlineKeyboard(chatId, markup, "Выберите упражнение:");

        } catch (NumberFormatException e) {
            updateConsumer.sendMessage(chatId, "Введите число повторов, например: 12");
        }
    }

    private void processWeightInput(UpdateConsumer updateConsumer, String input, Long chatId) {

        SetDto activeSet = setService.findFirstByActiveAndChatId(chatId.toString());
        try {
            double weight = Double.parseDouble(input);
            setService.saveWeight(activeSet, weight);
            updateConsumer.sendMessage(chatId, "Сохранил " + weight + " кг ✅");
        } catch (NumberFormatException e) {
            updateConsumer.sendMessage(chatId, "Введите рабочий вес (число или число с точкой, например: 80.5): ");
        }

        var button1 = InlineKeyboardButton.builder()
                .text("Отменить")
                .callbackData(START_SUPERSET.getCommand())
                .build();
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup(List.of(
                new InlineKeyboardRow(button1))
        );
        updateConsumer.sendMessageWithInlineKeyboard(chatId, markup, "Введите число повторов, например: 12");

    }
}

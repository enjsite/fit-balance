package ru.enjy.fit_balance.telegram.command;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;
import ru.enjy.fit_balance.model.dto.WorkoutDto;
import ru.enjy.fit_balance.model.entity.SetApproachType;
import ru.enjy.fit_balance.service.ExerciseService;
import ru.enjy.fit_balance.service.SupersetService;
import ru.enjy.fit_balance.service.WorkoutService;
import ru.enjy.fit_balance.telegram.bot.UpdateConsumer;

import java.util.ArrayList;
import java.util.List;

import static ru.enjy.fit_balance.telegram.command.CommandName.*;

@Slf4j
@Component
public class StartSupersetCommand implements Command {

    private final CommandName command = START_SUPERSET;
    private WorkoutService workoutService;
    private SupersetService supersetService;
    private ExerciseService exerciseService;

    public StartSupersetCommand(CommandContainer commandContainer,
                                WorkoutService workoutService,
                                SupersetService supersetService,
                                ExerciseService exerciseService) {
        commandContainer.setCommandMap(this);
        this.workoutService = workoutService;
        this.supersetService = supersetService;
        this.exerciseService = exerciseService;
    }

    @Override
    public void execute(UpdateConsumer updateConsumer, Long chatId, Long exerciseId) {

        WorkoutDto activeWorkout = workoutService.findFirstByActiveTrueAndUserChatId(chatId.toString());

        if (activeWorkout != null) {
            //!!!
            // Добавить проверку - если активный суперсет уже существует, возможно мы зашли сюда, чтобы выбрать другое упражнение
            var superset = supersetService.create(activeWorkout, SetApproachType.SUPERSET);
            var exercises = exerciseService.getAll();
            List<InlineKeyboardRow> exercisesButtons = new ArrayList<>();
            exercises.forEach(ex -> {
                var button = InlineKeyboardButton.builder()
                        .text(ex.getTitle())
                        .callbackData("/ex" + ex.getId().toString())
                        .build();
                exercisesButtons.add(new InlineKeyboardRow(button));
            });

            var button2 = InlineKeyboardButton.builder()
                    .text("Закончить тренировку")
                    .callbackData(FINISH_WORKOUT.getCommand())
                    .build();
            exercisesButtons.add(new InlineKeyboardRow(button2));
            InlineKeyboardMarkup markup = new InlineKeyboardMarkup(exercisesButtons);
            updateConsumer.sendMessageWithInlineKeyboard(chatId, markup, "Выберите упражнение:");

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

package ru.enjy.fit_balance.telegram.command;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;
import ru.enjy.fit_balance.model.dto.SupersetDto;
import ru.enjy.fit_balance.model.dto.WorkoutDto;
import ru.enjy.fit_balance.service.ExerciseService;
import ru.enjy.fit_balance.service.SetService;
import ru.enjy.fit_balance.service.SupersetService;
import ru.enjy.fit_balance.service.WorkoutService;
import ru.enjy.fit_balance.telegram.bot.UpdateConsumer;

import java.util.List;

import static ru.enjy.fit_balance.telegram.command.CommandName.START_SUPERSET;

@Slf4j
@Component
public class AddSetCommand implements Command {

    private final CommandName command = CommandName.ADD_SET;
    private WorkoutService workoutService;
    private SupersetService supersetService;
    private SetService setService;

    public AddSetCommand(CommandContainer commandContainer,
                         WorkoutService workoutService,
                         SupersetService supersetService,
                         SetService setService,
                         ExerciseService exerciseService) {
        commandContainer.setCommandMap(this);
        this.workoutService = workoutService;
        this.supersetService = supersetService;
        this.setService = setService;

        var exercises = exerciseService.getAll();
        exercises.forEach(exercise ->
                commandContainer.setCommandMap("/ex" + exercise.getId().toString(), this));
    }

    @Override
    public void execute(UpdateConsumer updateConsumer, Long chatId, Long exerciseId) {

        log.info("Зашли в Set Command");

        WorkoutDto activeWorkout = workoutService.findFirstByActiveTrueAndUserChatId(chatId.toString());
        SupersetDto activeSuperset = null;
        if (activeWorkout != null) {
            activeSuperset = supersetService.findFirstByActiveAndWorkout(activeWorkout);
        }

        if (activeSuperset != null) {
            var exerciseSet = setService.create(activeSuperset, exerciseId);

            var button1 = InlineKeyboardButton.builder()
                    .text("Отменить")
                    .callbackData(START_SUPERSET.getCommand())
                    .build();
            InlineKeyboardMarkup markup = new InlineKeyboardMarkup(List.of(
                    new InlineKeyboardRow(button1))
            );
            updateConsumer.sendMessageWithInlineKeyboard(chatId, markup,
                    "Введите рабочий вес (число или число с точкой, например: 80.5): ");

        } else {
            var button = InlineKeyboardButton.builder()
                    .text("Начать сет")
                    .callbackData(START_SUPERSET.getCommand())
                    .build();
            InlineKeyboardMarkup markup = new InlineKeyboardMarkup(List.of(new InlineKeyboardRow(button)));
            updateConsumer.sendMessageWithInlineKeyboard(chatId, markup, "У вас нет активных сетов. " +
                    "Если хотите продолжить, надо начать сет.");
        }
    }

    @Override
    public String getCommandName() {
        return command.getCommand();
    }
}

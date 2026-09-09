package ru.enjy.fit_balance.telegram.command;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;
import ru.enjy.fit_balance.model.dto.UserAccountDto;
import ru.enjy.fit_balance.model.entity.SessionState;
import ru.enjy.fit_balance.model.entity.Superset;
import ru.enjy.fit_balance.model.entity.Workout;
import ru.enjy.fit_balance.model.entity.WorkoutSession;
import ru.enjy.fit_balance.service.*;
import ru.enjy.fit_balance.service.report.WorkoutReportService;
import ru.enjy.fit_balance.service.session.WorkoutSessionService;
import ru.enjy.fit_balance.telegram.bot.UpdateConsumer;

import java.util.List;

import static ru.enjy.fit_balance.telegram.command.CommandName.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class AddSetCommand implements Command {

    private final CommandName command = CommandName.ADD_SET;
    private final SetService setService;
    private final UserAccountService userAccountService;
    private final WorkoutSessionService workoutSessionService;
    private final WorkoutReportService workoutReportService;

    private final CommandContainer commandContainer;

    @PostConstruct
    public void init() {
        commandContainer.setCommandMap(this);
    }

    @Override
    public void execute(UpdateConsumer updateConsumer, Long chatId, Long exerciseId) {

        System.out.println("AddSetCommand");

        UserAccountDto userAccountDto = userAccountService.findFirstByChatId(chatId.toString());
        WorkoutSession session = workoutSessionService.getRequired(userAccountDto.getId());
        Workout currentWorkout = session.getCurrentWorkout();

        if (exerciseId == null) {
            System.out.println("exercise не передан ");
            exerciseId = session.getCurrentExercise().getId();
        }

        Superset currentSuperset = null;
        if (currentWorkout != null) {
            currentSuperset = session.getCurrentSuperset();
        } else {
            // предложить начать workout
        }
        System.out.println("Зашли в AddSetCommand а что здесь дальше?");

        if (currentSuperset != null) { // здесь надо убедиться, что при завершении суперсета корректно сбросили его в сессии

            workoutSessionService.updateState(session, SessionState.WAITING_WEIGHT);

            var exerciseSet = setService.create(currentSuperset, exerciseId);

            var button1 = InlineKeyboardButton.builder()
                    .text("Отменить")
                    .callbackData(ADD_EXERCISE.getCommand()) // этого должно быть достаточно
                    .build();
            InlineKeyboardMarkup markup = new InlineKeyboardMarkup(List.of(
                    new InlineKeyboardRow(button1))
            );

            //updateConsumer.sendMessage(chatId, workoutReportService.getWorkoutLog(currentWorkout.getId()));
            //updateConsumer.sendMessageWithInlineKeyboard(chatId, markup,
              //      "Введите рабочий вес (число или число с точкой, например: 80.5): ");

            updateConsumer.updateWorkoutMessage(chatId, session.getMessageId(), markup,
                    workoutReportService.getWorkoutLog(currentWorkout.getId()) + "\n\nВведите рабочий вес (число или число с точкой, например: 80.5): ");

        } else {
            var button = InlineKeyboardButton.builder()
                    .text("Начать сет")
                    .callbackData(START_SINGLESET.getCommand())
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

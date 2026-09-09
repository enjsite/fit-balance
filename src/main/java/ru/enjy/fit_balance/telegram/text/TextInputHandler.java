package ru.enjy.fit_balance.telegram.text;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;
import ru.enjy.fit_balance.model.dto.SetDto;
import ru.enjy.fit_balance.model.dto.UserAccountDto;
import ru.enjy.fit_balance.model.entity.*;
import ru.enjy.fit_balance.model.mapper.UserAccountMapper;
import ru.enjy.fit_balance.repository.ExerciseRepository;
import ru.enjy.fit_balance.service.*;
import ru.enjy.fit_balance.service.report.WorkoutReportService;
import ru.enjy.fit_balance.service.session.WorkoutSessionService;
import ru.enjy.fit_balance.telegram.bot.UpdateConsumer;
import ru.enjy.fit_balance.telegram.command.Command;
import ru.enjy.fit_balance.telegram.command.CommandContainer;

import java.util.List;
import java.util.Optional;

import static ru.enjy.fit_balance.telegram.command.CommandName.*;


@Component
@RequiredArgsConstructor
@Slf4j
public class TextInputHandler {

    private final CommandContainer commandContainer;

    private final ExerciseService exerciseService;
    private final SetService setService;

    private final UserAccountService userAccountService;
    private final WorkoutSessionService workoutSessionService;
    private final UserAccountMapper userAccountMapper;
    private final ExerciseRepository exerciseRepository;
    private final WorkoutReportService workoutReportService;

    public void handle(UpdateConsumer updateConsumer, Long chatId, String message, Integer messageId) {

        UserAccountDto userAccountDto = userAccountService.findFirstByChatId(chatId.toString()); // to do сразу получать не dto
        UserAccount userAccount = userAccountMapper.toEntity(userAccountDto);
        WorkoutSession session = workoutSessionService.getOrRestore(userAccountDto.getId(), userAccount);

        log.info(session.getState().toString());
        switch (session.getState()) {
            case WAITING_EXERCISE_NAME -> processExerciseInput(updateConsumer, message, userAccount, session, messageId);
            case WAITING_REPS -> processRepsInput(updateConsumer, message, chatId, userAccount, session);
            case WAITING_WEIGHT -> processWeightInput(updateConsumer, message, chatId, session, messageId);
            default -> updateConsumer.sendMessage(chatId, "Неожиданный ввод: " + message);
        }
    }

    private void processExerciseInput(UpdateConsumer updateConsumer, String message, UserAccount user, WorkoutSession session, Integer messageId) {

        Optional<Exercise> existing = exerciseRepository.findFirstByTitleIgnoreCaseAndUserId(message, user.getId());

        Exercise exercise = existing.orElseGet(() -> exerciseService.create(message, user));
        workoutSessionService.attachExercise(session, exercise);

        // удаление пользовательского ввода после сохранения данных
        updateConsumer.deleteUserMessage(user.getChatId(), messageId);

        // вызываем ADD_SET.getCommand()
        var command = commandContainer.getCommand(ADD_SET.getCommand());
        command.execute(updateConsumer, Long.parseLong(user.getChatId()), exercise.getId());
    }

    private void processRepsInput(UpdateConsumer updateConsumer, String input, Long chatId, UserAccount user, WorkoutSession session) {

        SetDto activeSet = setService.findFirstByActiveAndChatId(chatId.toString());
        try {
            int reps = Integer.parseInt(input);
            setService.saveReps(activeSet, reps);
            System.out.println("processRepsInput сохраняем число повторов, а какой статус дальше тут установить?");

            // перенесу это непосредственно в команду
            workoutSessionService.updateState(session, SessionState.WAITING_WEIGHT); // или ввод упражнения? когда ожидается ввод упражнения?

            var button1 = InlineKeyboardButton.builder()
                    .text("Закончить сет")
                    .callbackData(FINISH_SUPERSET.getCommand())
                    .build();
            var button2 = InlineKeyboardButton.builder()
                    .text("Закончить тренировку")
                    .callbackData(FINISH_WORKOUT.getCommand())
                    .build();
            var button0 = InlineKeyboardButton.builder()
                    .text("+ Еще подход")
                    .callbackData(ADD_SET.getCommand())
                    .build();
            var button4 = InlineKeyboardButton.builder()
                    .text("+ Добавить упражнение в сет")
                    .callbackData(ADD_EXERCISE.getCommand())
                    .build();

            InlineKeyboardMarkup markup = new InlineKeyboardMarkup(List.of(
                    new InlineKeyboardRow(button0),
                    new InlineKeyboardRow(button4),
                    new InlineKeyboardRow(button1),
                    new InlineKeyboardRow(button2)));

            updateConsumer.sendMessage(chatId, workoutReportService.getWorkoutLog(session.getCurrentWorkout().getId()));
            updateConsumer.sendMessage(chatId, "Сохранил " + reps + " повторов ✅");

            updateConsumer.sendMessageWithInlineKeyboard(chatId, markup, "Что дальше?");

        } catch (NumberFormatException e) {
            updateConsumer.sendMessage(chatId, "Введите число повторов, например: 12");
        }
    }

    private void processWeightInput(UpdateConsumer updateConsumer, String input, Long chatId, WorkoutSession session, Integer messageId) {

        SetDto activeSet = setService.findFirstByActiveAndChatId(chatId.toString());
        try {
            double weight = Double.parseDouble(input);
            setService.saveWeight(activeSet, weight);

            var button1 = InlineKeyboardButton.builder()
                    .text("Отменить")
                    .callbackData(ADD_WEIGHT.getCommand()) // Новая команда ADD_WEIGHT
                    .build();
            InlineKeyboardMarkup markup = new InlineKeyboardMarkup(List.of(
                    new InlineKeyboardRow(button1))
            );

            System.out.println("processWeightInput ввели вес и устанавливаем WAITING_REPS");
            workoutSessionService.updateState(session, SessionState.WAITING_REPS);

            updateConsumer.sendMessage(chatId, workoutReportService.getWorkoutLog(session.getCurrentWorkout().getId()));
            updateConsumer.sendMessage(chatId, "Сохранил " + weight + " кг ✅");
            updateConsumer.sendMessageWithInlineKeyboard(chatId, markup, "Введите число повторов, например: 12");

        } catch (NumberFormatException e) {
            updateConsumer.sendMessage(chatId, "Введите рабочий вес (число или число с точкой, например: 80.5): ");
        }
    }
}
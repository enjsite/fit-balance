package ru.enjy.fit_balance.telegram.command;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;
import ru.enjy.fit_balance.model.dto.SupersetDto;
import ru.enjy.fit_balance.model.dto.UserAccountDto;
import ru.enjy.fit_balance.model.dto.WorkoutDto;
import ru.enjy.fit_balance.model.entity.Superset;
import ru.enjy.fit_balance.model.entity.Workout;
import ru.enjy.fit_balance.model.entity.WorkoutSession;
import ru.enjy.fit_balance.model.mapper.SupersetMapper;
import ru.enjy.fit_balance.service.*;
import ru.enjy.fit_balance.service.report.WorkoutReportService;
import ru.enjy.fit_balance.service.session.WorkoutSessionService;
import ru.enjy.fit_balance.telegram.bot.UpdateConsumer;

import java.util.List;

import static ru.enjy.fit_balance.telegram.command.CommandName.START_SUPERSET;

@Slf4j
@Component
@RequiredArgsConstructor
public class AddSetCommand implements Command {

    private final CommandName command = CommandName.ADD_SET;
    private final WorkoutService workoutService;
    private final SupersetService supersetService;
    private final SetService setService;
    private final ExerciseService exerciseService;
    private final UserAccountService userAccountService;
    private final WorkoutSessionService workoutSessionService;
    private final SupersetMapper supersetMapper;
    private final WorkoutReportService workoutReportService;

    private final CommandContainer commandContainer;

    @PostConstruct
    public void init() {
        commandContainer.setCommandMap(this);

        // точно ли это надо?
        // to do getAll только для этого юзера ?
        var exercises = exerciseService.getAll();
        exercises.forEach(exercise ->
                commandContainer.setCommandMap("/ex" + exercise.getId().toString(), this));
    }

    @Override
    public void execute(UpdateConsumer updateConsumer, Long chatId, Long exerciseId) {

        System.out.println("AddSetCommand");

        // получить сессию, воркаут, суперсет и exerciseId - достать из сессии, если не передано.
        UserAccountDto userAccountDto = userAccountService.findFirstByChatId(chatId.toString());
        WorkoutSession session = workoutSessionService.getRequired(userAccountDto.getId());
        Workout currentWorkout = session.getCurrentWorkout();

        if (exerciseId == null) {
            System.out.println("exercise не передан ");
            exerciseId = session.getCurrentExercise().getId();
            // должно ли быть создание подхода командой, если каждый раз оно вызывается напрямую через execute
        }

        //WorkoutDto activeWorkout = workoutService.findFirstByActiveTrueAndUserChatId(chatId.toString());
        Superset currentSuperset = null;
        if (currentWorkout != null) {
            // суперсет будет тот же и мы должны достать его из сессии
            //currentSuperset = supersetService.findFirstByActiveAndWorkout(currentWorkout);
            currentSuperset = session.getCurrentSuperset();
        } else {
            // предложить начать workout
        }
        System.out.println("Зашли в AddSetCommand а что здесь дальше?");

        if (currentSuperset != null) { // здесь надо убедиться, что при завершении суперсета корректно сбросили его в сессии

            // убрать дто
            //var supersetDto = supersetMapper.toSupersetDto(currentSuperset);
            var exerciseSet = setService.create(currentSuperset, exerciseId);
            System.out.println("какой статус у сессии? " + session.getState());

            var button1 = InlineKeyboardButton.builder()
                    .text("Отменить")
                    .callbackData(START_SUPERSET.getCommand())
                    .build();
            InlineKeyboardMarkup markup = new InlineKeyboardMarkup(List.of(
                    new InlineKeyboardRow(button1))
            );

            updateConsumer.sendMessage(chatId, workoutReportService.getWorkoutLog(currentWorkout.getId()));
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

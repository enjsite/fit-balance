package ru.enjy.fit_balance.telegram.command;

import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;
import ru.enjy.fit_balance.model.dto.UserAccountDto;
import ru.enjy.fit_balance.model.dto.WorkoutDto;
import ru.enjy.fit_balance.model.entity.SessionState;
import ru.enjy.fit_balance.model.entity.SetApproachType;
import ru.enjy.fit_balance.model.entity.Workout;
import ru.enjy.fit_balance.model.entity.WorkoutSession;
import ru.enjy.fit_balance.model.mapper.UserAccountMapper;
import ru.enjy.fit_balance.service.ExerciseService;
import ru.enjy.fit_balance.service.SupersetService;
import ru.enjy.fit_balance.service.UserAccountService;
import ru.enjy.fit_balance.service.WorkoutService;
import ru.enjy.fit_balance.service.session.WorkoutSessionService;
import ru.enjy.fit_balance.telegram.bot.UpdateConsumer;

import java.util.ArrayList;
import java.util.List;

import static ru.enjy.fit_balance.telegram.command.CommandName.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class StartSinglesetCommand implements Command {

    private final CommandName command = START_SINGLESET;
    private final WorkoutService workoutService;
    private final SupersetService supersetService;
    private final ExerciseService exerciseService;
    private final UserAccountService userAccountService;
    private final WorkoutSessionService workoutSessionService;
    private final UserAccountMapper userAccountMapper;
    private final CommandContainer commandContainer;

    @PostConstruct
    public void init() {
        commandContainer.setCommandMap(this);
    }

    @Override
    public void execute(UpdateConsumer updateConsumer, Long chatId, Long exerciseId) {

        //получить пользователя и его сессию и проверить hasInProgressWorkoutContext
        //и если нет - отправить на начать тренировку
        // не стоит ли привязать сессию к chatId?
        UserAccountDto userAccountDto = userAccountService.findFirstByChatId(chatId.toString());
        WorkoutSession session = workoutSessionService.getRequired(userAccountDto.getId());
        Workout currentWorkout = session.getCurrentWorkout();

        //WorkoutDto activeWorkout = workoutService.findFirstByActiveTrueAndUserChatId(chatId.toString());

        if (currentWorkout != null) {
            //!!!
            // Добавить проверку - если активный суперсет уже существует, возможно мы зашли сюда, чтобы выбрать другое упражнение
            var currentSuperset = supersetService.create(currentWorkout, SetApproachType.SET);

            workoutSessionService.attachSuperset(session, currentSuperset);
            System.out.println("in StartSinglesetCommand добавила суперсет и ставлю ожидание ввода названия упражнения хотя он и так может уже стоять " + session.getState());


            /*var exercises = exerciseService.getAll();
            List<InlineKeyboardRow> exercisesButtons = new ArrayList<>();
            exercises.forEach(ex -> {
                var button = InlineKeyboardButton.builder()
                        .text(ex.getTitle())
                        .callbackData("/ex" + ex.getId().toString())
                        .build();
                exercisesButtons.add(new InlineKeyboardRow(button));
            });*/

            var button2 = InlineKeyboardButton.builder()
                    .text("Закончить тренировку")
                    .callbackData(FINISH_WORKOUT.getCommand())
                    .build();
            /*exercisesButtons.add(new InlineKeyboardRow(button2));
            InlineKeyboardMarkup markup = new InlineKeyboardMarkup(exercisesButtons);
            updateConsumer.sendMessageWithInlineKeyboard(chatId, markup, "Выберите упражнение:");*/


            var button1 = InlineKeyboardButton.builder()
                    .text("Отменить")
                    .callbackData(START_SUPERSET.getCommand())
                    .build();
            InlineKeyboardMarkup markup = new InlineKeyboardMarkup(List.of(
                    new InlineKeyboardRow(button1), new InlineKeyboardRow(button2))
            );
            updateConsumer.sendMessageWithInlineKeyboard(chatId, markup,
                    "Введите название упражения: ");


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

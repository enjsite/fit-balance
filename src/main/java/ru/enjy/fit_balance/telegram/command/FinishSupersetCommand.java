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
import ru.enjy.fit_balance.model.mapper.SupersetMapper;
import ru.enjy.fit_balance.service.SupersetService;
import ru.enjy.fit_balance.service.UserAccountService;
import ru.enjy.fit_balance.service.report.WorkoutReportService;
import ru.enjy.fit_balance.service.session.WorkoutSessionService;
import ru.enjy.fit_balance.telegram.bot.UpdateConsumer;

import java.util.List;

import static ru.enjy.fit_balance.telegram.command.CommandName.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class FinishSupersetCommand implements Command {

    private final CommandName command = FINISH_SUPERSET;
    private final SupersetService supersetService;
    private final CommandContainer commandContainer;
    private final UserAccountService userAccountService;
    private final WorkoutSessionService workoutSessionService;
    private final WorkoutReportService workoutReportService;

    private final SupersetMapper supersetMapper;

    @PostConstruct
    public void init() {
        commandContainer.setCommandMap(this);
    }

    @Override
    public void execute(UpdateConsumer updateConsumer, Long chatId, Long exerciseId) {

        System.out.println("FinishSupersetCommand");

        UserAccountDto userAccountDto = userAccountService.findFirstByChatId(chatId.toString());
        WorkoutSession session = workoutSessionService.getRequired(userAccountDto.getId());
        Workout currentWorkout = session.getCurrentWorkout();

        if (currentWorkout != null) {
            Superset currentSuperset = session.getCurrentSuperset();
            if (currentSuperset != null) {
                supersetService.finishSuperset(currentSuperset.getId());
                workoutSessionService.clearSuperset(session);
                workoutSessionService.updateState(session, SessionState.WAITING_SUPERSET);
            }

            var button0 = InlineKeyboardButton.builder()
                    .text("Начать сет")
                    .callbackData(START_SINGLESET.getCommand())
                    .build();
            var button2 = InlineKeyboardButton.builder()
                    .text("Закончить тренировку")
                    .callbackData(FINISH_WORKOUT.getCommand())
                    .build();
            InlineKeyboardMarkup markup = new InlineKeyboardMarkup(
                    List.of(
                            new InlineKeyboardRow(button0),
                            new InlineKeyboardRow(button2)
                    ));

            updateConsumer.updateWorkoutMessage(chatId, session.getMessageId(), markup,
                    workoutReportService.getWorkoutLog(currentWorkout.getId()) + "\n\nСет завершен. Выберите действие: ");

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

package ru.enjy.fit_balance.telegram.command;

import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;
import ru.enjy.fit_balance.model.dto.UserAccountDto;
import ru.enjy.fit_balance.model.entity.Workout;
import ru.enjy.fit_balance.model.entity.WorkoutSession;
import ru.enjy.fit_balance.model.mapper.UserAccountMapper;
import ru.enjy.fit_balance.model.mapper.WorkoutMapper;
import ru.enjy.fit_balance.service.UserAccountService;
import ru.enjy.fit_balance.service.WorkoutService;
import ru.enjy.fit_balance.service.report.WorkoutReportService;
import ru.enjy.fit_balance.service.session.WorkoutSessionService;
import ru.enjy.fit_balance.telegram.bot.UpdateConsumer;

import java.util.List;

import static ru.enjy.fit_balance.telegram.command.CommandName.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class StartWorkoutCommand implements Command {

    private final CommandName command = START_WORKOUT;
    private final WorkoutService workoutService;
    private final UserAccountService userAccountService;
    private final WorkoutSessionService workoutSessionService;
    private final UserAccountMapper userAccountMapper;
    private final WorkoutReportService workoutReportService;

    private final CommandContainer commandContainer;

    @PostConstruct
    public void init() {
        commandContainer.setCommandMap(this);
    }

    @Override
    @Transactional
    public void execute(UpdateConsumer updateConsumer, Long chatId, Long exerciseId) {

        UserAccountDto userAccountDto = userAccountService.findFirstByChatId(chatId.toString());
        Workout workout;

        // получаем сессию пользователя
        WorkoutSession session = workoutSessionService.getOrCreate(userAccountDto.getId(), userAccountMapper.toEntity(userAccountDto));

        if (!workoutSessionService.hasInProgressWorkoutContext(session)) {
            workout = workoutService.create(userAccountDto);
        } else {
            workout = workoutService.findInProgressWorkoutByUserId(userAccountDto.getId())
                    .orElseThrow(() ->
                            new IllegalStateException("Active workout expected"));
        }

        workoutSessionService.attachWorkout(session, workout);

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

        // тут должен быть полный лог
        updateConsumer.sendMessage(chatId, workoutReportService.getWorkoutLog(workout.getId()));
        updateConsumer.sendMessageWithInlineKeyboard(chatId, markup, "Выберите действие:");
    }

    @Override
    public String getCommandName() {
        return command.getCommand();
    }
}

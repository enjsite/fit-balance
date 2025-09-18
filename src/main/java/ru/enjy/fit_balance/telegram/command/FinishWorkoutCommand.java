package ru.enjy.fit_balance.telegram.command;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;
import ru.enjy.fit_balance.model.dto.WorkoutDto;
import ru.enjy.fit_balance.service.WorkoutService;
import ru.enjy.fit_balance.service.report.WorkoutReportService;
import ru.enjy.fit_balance.telegram.bot.UpdateConsumer;

import java.util.List;

import static ru.enjy.fit_balance.telegram.command.CommandName.*;

@Component
public class FinishWorkoutCommand implements Command {

    private final CommandName command = FINISH_WORKOUT;
    private final WorkoutService workoutService;

    private final WorkoutReportService workoutReportService;

    public FinishWorkoutCommand(CommandContainer commandContainer,
                                WorkoutService workoutService,
                                WorkoutReportService workoutReportService) {
        commandContainer.setCommandMap(this);
        this.workoutService = workoutService;
        this.workoutReportService = workoutReportService;
    }

    @Override
    public void execute(UpdateConsumer updateConsumer, Long chatId, Long exerciseId) {

        WorkoutDto activeWorkout = workoutService.findFirstByActiveTrueAndUserChatId(chatId.toString());
        if (activeWorkout != null) {
            workoutService.finishWorkout(activeWorkout);
        } else {
            updateConsumer.sendMessage(chatId, "Невозможно завершить тренировку - нет ни одной активной.");
        }

        updateConsumer.sendMessage(chatId, "\uD83C\uDFC6✨ *Тренировка завершена!* ✨\uD83C\uDFC6 " +
                "\n\uD83D\uDCAA Отличная работа, так держать! \uD83C\uDF1F \n\n" +
                workoutReportService.getWorkoutReport(activeWorkout.getId()));

        // todo: вывести в отдельном треде с задержкой в 5-10 минут
        var button = InlineKeyboardButton.builder()
                .text("Начать тренировку")
                .callbackData(START_WORKOUT.getCommand())
                .build();
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup(List.of(new InlineKeyboardRow(button)));
        updateConsumer.sendMessageWithInlineKeyboard(chatId, markup, "\uD83D\uDE80 Время прокачки!\n" +
                "Нажми «Начать тренировку», и записывай упражнения, веса и повторы, чтобы следить за прогрессом.");
    }

    @Override
    public String getCommandName() {
        return command.getCommand();
    }
}

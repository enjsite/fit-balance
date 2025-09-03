package ru.enjy.fit_balance.telegram.command;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;
import ru.enjy.fit_balance.model.dto.SupersetDto;
import ru.enjy.fit_balance.model.dto.UserAccountDto;
import ru.enjy.fit_balance.model.dto.WorkoutDto;
import ru.enjy.fit_balance.service.SupersetService;
import ru.enjy.fit_balance.service.UserAccountService;
import ru.enjy.fit_balance.service.WorkoutService;
import ru.enjy.fit_balance.telegram.bot.UpdateConsumer;

import java.util.List;

import static ru.enjy.fit_balance.telegram.command.CommandName.SUPERSET;
import static ru.enjy.fit_balance.telegram.command.CommandName.WORKOUT;

@Slf4j
@Component
public class SupersetCommand implements Command {

    private final CommandName command = SUPERSET;
    private WorkoutService workoutService;
    private SupersetService supersetService;

    public SupersetCommand(CommandContainer commandContainer,
                           WorkoutService workoutService,
                           SupersetService supersetService) {
        commandContainer.setCommandMap(this);
        this.workoutService = workoutService;
        this.supersetService = supersetService;
    }

    @Override
    public void execute(UpdateConsumer updateConsumer, Long chatId) {

        log.info("ищем активную тренировку для user chat id " + chatId.toString());
        WorkoutDto activeWorkout = workoutService.findFirstByActiveTrueAndUserChatId(chatId.toString());
        log.info(activeWorkout.getId().toString());

        if (activeWorkout != null) {
            var superset = supersetService.create(activeWorkout);
            var button1 = InlineKeyboardButton.builder()
                    .text("Обратные отжимания")
                    .callbackData("/set")
                    .build();
            var button2 = InlineKeyboardButton.builder()
                    .text("Закончить тренировку")
                    .callbackData("/help")
                    .build();
            InlineKeyboardMarkup markup = new InlineKeyboardMarkup(List.of(
                    new InlineKeyboardRow(button1),
                    new InlineKeyboardRow(button2)));
            updateConsumer.sendMessageWithInlineKeyboard(chatId, markup, "Выберите упражнение:");

        } else {
            var button = InlineKeyboardButton.builder()
                    .text("Начать тренировку")
                    .callbackData("/workout")
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

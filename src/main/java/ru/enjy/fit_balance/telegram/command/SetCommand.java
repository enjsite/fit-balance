package ru.enjy.fit_balance.telegram.command;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;
import ru.enjy.fit_balance.model.dto.SupersetDto;
import ru.enjy.fit_balance.model.dto.WorkoutDto;
import ru.enjy.fit_balance.service.SetService;
import ru.enjy.fit_balance.service.SupersetService;
import ru.enjy.fit_balance.service.WorkoutService;
import ru.enjy.fit_balance.telegram.bot.UpdateConsumer;

import java.util.List;

@Slf4j
@Component
public class SetCommand implements Command {

    private final CommandName command = CommandName.SET;
    private WorkoutService workoutService;
    private SupersetService supersetService;
    private SetService setService;

    public SetCommand(CommandContainer commandContainer,
                      WorkoutService workoutService,
                      SupersetService supersetService,
                      SetService setService) {
        commandContainer.setCommandMap(this);
        this.workoutService = workoutService;
        this.supersetService = supersetService;
        this.setService = setService;
    }

    @Override
    public void execute(UpdateConsumer updateConsumer, Long chatId, Long exerciseId) {

        WorkoutDto activeWorkout = workoutService.findFirstByActiveTrueAndUserChatId(chatId.toString());
        SupersetDto activeSuperset = null;
        if (activeWorkout != null) {
            activeSuperset = supersetService.findFirstByActiveAndWorkout(activeWorkout.getId());
        }

        if (activeSuperset != null) {
            //var exerciseSet = setService.create(activeSuperset); //создать метод

            var button2 = InlineKeyboardButton.builder()
                    .text("Закончить тренировку")
                    .callbackData("/help")
                    .build();
            InlineKeyboardMarkup markup = new InlineKeyboardMarkup(List.of(
                    //new InlineKeyboardRow(button1),
                    new InlineKeyboardRow(button2)));
            updateConsumer.sendMessageWithInlineKeyboard(chatId, markup, "Надо заполнить данные о количестве повторов и весе нагрузки:");

        } else {
            var button = InlineKeyboardButton.builder()
                    .text("Начать сет")
                    .callbackData("/superset")
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

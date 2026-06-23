package ru.enjy.fit_balance.telegram.command;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;
import ru.enjy.fit_balance.service.UserAccountService;
import ru.enjy.fit_balance.telegram.bot.UpdateConsumer;

import java.util.List;
import java.nio.file.Paths;

import static ru.enjy.fit_balance.telegram.command.CommandName.START;
import static ru.enjy.fit_balance.telegram.command.CommandName.START_WORKOUT;

@Component
public class StartCommand implements Command {

    private final CommandName command = START;
    private final UserAccountService userAccountService;

    public StartCommand(CommandContainer commandContainer, UserAccountService userAccountService) {
        commandContainer.setCommandMap(this);
        this.userAccountService = userAccountService;
    }

    @Override
    public void execute(UpdateConsumer updateConsumer, Long chatId, Long exerciseId) {

        var message = "";

        if (!userAccountService.hasUserWithChatId(chatId.toString())) {
            userAccountService.create(chatId.toString());
            message = "Вы успешно зарегистрированы!";
        } else {
            message = "Привет! У вас уже есть аккаунт.";
        }
        updateConsumer.sendMessage(chatId, message);

        var imageInput = updateConsumer.createImageInputStream(Paths.get("/opt/app/img/trenirovka_nog_v_trenazhernom_zale.jpg"));
        if (imageInput != null) {
            updateConsumer.sendImage(chatId, imageInput);
        }
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

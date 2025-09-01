package ru.enjy.fit_balance.telegram.command;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;
import ru.enjy.fit_balance.telegram.bot.UpdateConsumer;

import java.util.List;

import static ru.enjy.fit_balance.telegram.command.CommandName.START;

@Component
public class StartCommand implements Command {

    private final CommandName command = START;

    public StartCommand(CommandContainer commandContainer) {
        commandContainer.setCommandMap(this);
    }

    @Override
    public void execute(UpdateConsumer updateConsumer, Long chatId) {

        var button = InlineKeyboardButton.builder()
                .text("Присоединиться!")
                .callbackData("/registration")
                .build();

        InlineKeyboardMarkup markup = new InlineKeyboardMarkup(List.of(new InlineKeyboardRow(button)));
        updateConsumer.sendMessageWithInlineKeyboard(chatId, markup,"Привет! Хотите начать тренировку?");
    }

    @Override
    public String getCommandName() {
        return command.getCommand();
    }
}

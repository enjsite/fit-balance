package ru.enjy.fit_balance.telegram.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;
import ru.enjy.fit_balance.model.dto.UserAccountDto;
import ru.enjy.fit_balance.service.UserAccountService;
import ru.enjy.fit_balance.telegram.bot.UpdateConsumer;

import java.nio.file.Paths;
import java.util.List;

import static ru.enjy.fit_balance.telegram.command.CommandName.REGISTRATION;

@Slf4j
@Component
public class RegistrationCommand implements Command {

    private final CommandName command = REGISTRATION;
    private final UserAccountService userAccountService;

    public RegistrationCommand(CommandContainer commandContainer, UserAccountService userAccountService) {
        commandContainer.setCommandMap(this);
        this.userAccountService = userAccountService;
    }

    @Override
    public void execute(UpdateConsumer updateConsumer, Long chatId, Long exerciseId) {

        var message = "Вы успешно зарегистрированы!";

        if (!userAccountService.hasUserWithChatId(chatId.toString())) {
            userAccountService.create(chatId.toString());
        } else {
            message = "У вас уже есть аккаунт.";
        }

        var imageInput = updateConsumer.createImageInputStream(Paths.get("/opt/app/img/trenirovka_nog_v_trenazhernom_zale.jpg"));
        if (imageInput != null) {
            updateConsumer.sendImage(chatId, imageInput);
        }
        var button = InlineKeyboardButton.builder()
                .text("Начать тренировку")
                .callbackData("/workout")
                .build();
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup(List.of(new InlineKeyboardRow(button)));
        updateConsumer.sendMessageWithInlineKeyboard(chatId, markup, message);
    }

    @Override
    public String getCommandName() {
        return command.getCommand();
    }
}

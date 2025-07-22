package ru.enjy.fit_balance.telegram;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;
import ru.enjy.fit_balance.service.UserAccountService;
import ru.enjy.fit_balance.service.WorkoutService;
import ru.enjy.fit_balance.telegram.bot.MultiSessionTelegramBot;
import ru.enjy.fit_balance.telegram.command.CommandContainer;

@Service
public class TelegramBotService extends MultiSessionTelegramBot {

    public static final String NAME = "FitBalanceFromENJYTestBot";
    public static final String TOKEN = "8150171260:AAFAmLVkw-l7otHmxwWRpbw4ecq-vMHVEac";

    private final TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);

    private final CommandContainer commandContainer;

    @PostConstruct
    public void init() {
        try {
            telegramBotsApi.registerBot(this);
            System.out.println("Telegram bot has been registered! " + this.getBotUsername());
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    @Autowired
    public TelegramBotService(UserAccountService userAccountService,
                              WorkoutService workoutService) throws TelegramApiException {
        super(NAME, TOKEN);
        this.commandContainer = new CommandContainer(this, userAccountService, workoutService);
    }


    @Override
    public void onUpdateEventReceived(Update updateEvent) {

        String message = getCallbackQueryButtonKey().isEmpty() ? getMessageText() : getCallbackQueryButtonKey();
        String command = message.split(" ")[0].toLowerCase();

        commandContainer.retrieveCommand(command).execute(updateEvent);
    }


}

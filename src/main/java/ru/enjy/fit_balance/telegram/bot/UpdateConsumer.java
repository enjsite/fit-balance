package ru.enjy.fit_balance.telegram.bot;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.generics.TelegramClient;
import ru.enjy.fit_balance.service.state.WorkoutStateService;
import ru.enjy.fit_balance.telegram.command.Command;
import ru.enjy.fit_balance.telegram.command.CommandContainer;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Slf4j
@Component
public class UpdateConsumer implements LongPollingSingleThreadUpdateConsumer {

    private final TelegramClient telegramClient;
    private final CommandContainer commandContainer;
    private final WorkoutStateService stateService;

    public UpdateConsumer(@Value("${telegram.bot.token}") String token,
                          CommandContainer commandContainer,
                          WorkoutStateService stateService) {
        this.telegramClient = new OkHttpTelegramClient(token);
        this.commandContainer = commandContainer;
        this.stateService = stateService;
    }

    @SneakyThrows
    @Override
    public void consume(Update update) {

        Long chatId = getChatId(update);
        if (chatId == null) return;

        String query = getQuery(update);
        handleQuery(chatId, query);
    }

    private void handleQuery(Long chatId, String message) {
        // разбить на методы

        if (message.startsWith("/")) {

            Long exerciseId = null;
            if (message.startsWith("/ex")) {
                try {
                    exerciseId = Long.parseLong(message.replace("/ex", ""));
                } catch (NumberFormatException ignored) {}
            }

            var command = getCommand(message);
            if (command != null) {
                command.execute(this, chatId, exerciseId);
            } else {
                sendMessage(chatId, "Неизвестная команда");
            }
        } else {
            // обрабатываем пользовательский ввод в зависимости от состояния

            var state = stateService.getCurrentWorkoutInputState(chatId.toString());
            log.info(state.toString());
        }

    }

    private Long getChatId(Update update) {
        return update.hasCallbackQuery() ?
                update.getCallbackQuery().getFrom().getId() : update.hasMessage() ? update.getMessage().getChatId() : null;
    }

    private String getQuery(Update update) {
        return update.hasCallbackQuery() ?
                getCallbackQueryData(update) : getMessage(update);
    }

    private String getCallbackQueryData(Update update) {
        return update.hasCallbackQuery() ?
                update.getCallbackQuery().getData() : null;
    }

    private String getMessage(Update update) {
        return update.hasMessage() ?
                update.getMessage().getText() : null;
    }

    private Command getCommand(String command) {
        return commandContainer.getCommand(command);
    }

    @SneakyThrows
    private void sendReplyKeyboard(Long chatId) {
        SendMessage sendMessage = SendMessage.builder()
                .chatId(chatId.toString())
                .text("Это пример обычной клавиатуры:")
                .build();

        List<KeyboardRow> keyboardRows = List.of(
                new KeyboardRow("Привет", "Картинка")
        );

        ReplyKeyboardMarkup markup = new ReplyKeyboardMarkup(keyboardRows);
        markup.setResizeKeyboard(true);
        sendMessage.setReplyMarkup(markup);

        telegramClient.execute(sendMessage);
    }

    @SneakyThrows
    public void sendMessageWithInlineKeyboard(Long chatId, InlineKeyboardMarkup markup, String message) {

        SendMessage sendMessage = SendMessage.builder()
                .text(message)
                .chatId(chatId)
                .build();

        sendMessage.setReplyMarkup(markup);
        telegramClient.execute(sendMessage);
    }

    @SneakyThrows
    public void sendMessage(Long chatId, String message) {

        SendMessage sendMessage = SendMessage.builder()
                .text(message)
                .chatId(chatId)
                .build();

        telegramClient.execute(sendMessage);
    }

    @SneakyThrows
    public void sendImage(Long chatId, InputStream inputStream) {

        SendPhoto sendPhoto = SendPhoto.builder()
                .chatId(chatId)
                .photo(new InputFile(inputStream, "random.jpg"))
                .build();

        telegramClient.execute(sendPhoto);
    }

    @SneakyThrows
    public void sendImageWithDescription(Long chatId, InputStream inputStream, String description) {

        SendPhoto sendPhoto = SendPhoto.builder()
                .chatId(chatId)
                .photo(new InputFile(inputStream, "random.jpg"))
                .caption(description)
                .build();

        telegramClient.execute(sendPhoto);
    }

    public InputStream createImageInputStream(String name) {
        try {
            return Files.newInputStream(Paths.get("/opt/app/img/" + name));
        } catch (Exception e) {
            throw new RuntimeException("Can't create image!");
        }
    }

    public InputStream createImageInputStream(Path path) {
        try {
            return Files.newInputStream(path);
        } catch (IOException e) {
            throw new RuntimeException("Can't create image!");
        }
    }

    private void handleCallbackQuery(CallbackQuery callbackQuery) {
        var data = callbackQuery.getData();
        var chatId = callbackQuery.getFrom().getId();
        var user = callbackQuery.getFrom();
        switch (data) {
            case "my_name" -> sendMyName(chatId, user);
            case "random" -> sendRandom(chatId);
            case "long_process" -> sendDownloadedImage(chatId);
            default -> sendMessage(chatId, "Неизвестная команда");
        }
    }

    private void sendRandom(Long chatId) {
        var randomInt = ThreadLocalRandom.current().nextInt();
        sendMessage(chatId, "Ваше рандомное число: " + randomInt);
    }

    private void sendMyName(
            Long chatId,
            User user
    ) {
        var text = "Привет!\n\nВас зовут: %s\nВаш ник: @%s"
                .formatted(
                        user.getFirstName() + " " + user.getLastName(),
                        user.getUserName()
                );
        sendMessage(chatId, text);
    }

    private void sendDownloadedImage(Long chatId) {
        sendMessage(chatId, "Запустили загрузку картинки");
        new Thread(() -> {
            var imageUrl = "https://images.unsplash.com/photo-1701114413455-875c598cd407?w=1200";
            try {
                URL url = new URL(imageUrl);
                var inputStream = url.openStream();
                sendImageWithDescription(chatId, inputStream, "Случайная картинка");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }).start();
    }

    @SneakyThrows
    public void sendMainMenu(Long chatId) {
        SendMessage message = SendMessage.builder()
                .text("Добро пожаловать! Выберите действие:")
                .chatId(chatId)
                .build();

        var button1 = InlineKeyboardButton.builder()
                .text("Как меня зовут?")
                .callbackData("my_name")
                .build();

        var button2 = InlineKeyboardButton.builder()
                .text("Случайное число")
                .callbackData("random")
                .build();

        var button3 = InlineKeyboardButton.builder()
                .text("Долгий процесс")
                .callbackData("long_process")
                .build();

        List<InlineKeyboardRow> keyboardRows = List.of(
                new InlineKeyboardRow(button1),
                new InlineKeyboardRow(button2),
                new InlineKeyboardRow(button3)
        );

        InlineKeyboardMarkup markup = new InlineKeyboardMarkup(keyboardRows);

        message.setReplyMarkup(markup);

        telegramClient.execute(message);
    }


}

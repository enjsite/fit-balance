package ru.enjy.fit_balance.telegram.bot;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.message.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;
import ru.enjy.fit_balance.telegram.command.Command;
import ru.enjy.fit_balance.telegram.command.CommandContainer;
import ru.enjy.fit_balance.telegram.text.TextInputHandler;

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

    private final TextInputHandler textInputHandler;

    private final TelegramClient telegramClient;
    private final CommandContainer commandContainer;

    public UpdateConsumer(@Value("${telegram.bot.token}") String token,
                          CommandContainer commandContainer,
                          TextInputHandler textInputHandler) {
        this.telegramClient = new OkHttpTelegramClient(token);
        this.commandContainer = commandContainer;
        this.textInputHandler = textInputHandler;
    }

    @SneakyThrows
    @Override
    public void consume(Update update) {

        Long chatId = getChatId(update);
        if (chatId == null) return;

        String query = getQuery(update);
        Integer messageId = getMessageId(update);
        handleQuery(chatId, query, messageId);
    }

    private void handleQuery(Long chatId, String message, Integer messageId) {
        //команда
        if (message.startsWith("/")) {
            Long exerciseId = null;
            if (message.startsWith("/ex")) {
                try {
                    exerciseId = Long.parseLong(message.replace("/ex", ""));
                } catch (NumberFormatException ignored) {
                }
            }

            var command = getCommand(message);
            System.out.println("команда " + message);
            if (command != null) {
                command.execute(this, chatId, exerciseId);
            } else {
                sendMessage(chatId, "Неизвестная команда");
            }
        } else { // текстовый ввод
            textInputHandler.handle(this, chatId, message, messageId);
        }

    }

    private Long getChatId(Update update) {
        return update.hasCallbackQuery() ?
                update.getCallbackQuery().getFrom().getId() : update.hasMessage() ? update.getMessage().getChatId() : null;
    }

    private Integer getMessageId(Update update) {
        return update.hasCallbackQuery() ?
                update.getCallbackQuery().getMessage().getMessageId() : update.hasMessage() ? update.getMessage().getMessageId() : null;
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
        update.getMessage().getMessageId();
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
        // markup.setResizeKeyboard(true);
        //sendMessage.setReplyMarkup(markup);

        telegramClient.execute(sendMessage);
    }

    @SneakyThrows
    public Message sendMessageWithInlineKeyboard(Long chatId, InlineKeyboardMarkup markup, String message) {

        SendMessage sendMessage = SendMessage.builder()
                .text(message)
                .chatId(chatId)
                .build();

        sendMessage.setReplyMarkup(markup);
        return telegramClient.execute(sendMessage);
    }

    @SneakyThrows
    public void updateWorkoutMessage(Long chatId, Integer messageId, InlineKeyboardMarkup markup, String message) {
        if (messageId == null) {
            sendMessageWithInlineKeyboard(chatId, markup, message);
            return;
        }

        EditMessageText editMessage = EditMessageText.builder()
                .text(message)
                .chatId(chatId.toString())
                .messageId(messageId)
                .parseMode(org.telegram.telegrambots.meta.api.methods.ParseMode.MARKDOWN)
                .build();

        editMessage.setReplyMarkup(markup);
        telegramClient.execute(editMessage);
    }

    @SneakyThrows
    public void sendMessage(Long chatId, String message) {

        SendMessage sendMessage = SendMessage.builder()
                .text(message)
                .chatId(chatId)
                .parseMode(org.telegram.telegrambots.meta.api.methods.ParseMode.MARKDOWN)
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

    @SneakyThrows
    public void deleteUserMessage(String chatId, Integer messageId) {
        DeleteMessage deleteMessage = DeleteMessage.builder()
                .chatId(chatId)
                .messageId(messageId)
                .build();
        telegramClient.execute(deleteMessage);
        // подумать над обработкой ошибок вместо SneakyThrows
//        try {
//            telegramClient.execute(deleteMessage);
//            // Удаление прошло успешно (в личном чате это почти всегда так)
//        } catch (TelegramApiException e) {
//            // Логируй ошибку. Возможно, сообщение уже было удалено или прошло больше 48 часов
//            log.warn("Не удалось удалить сообщение {} в чате {}: {}", messageId, chatId, e.getMessage());
//        }
    }
}

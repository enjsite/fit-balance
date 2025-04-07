package ru.enjy.fit_balance.service;

//import jakarta.enterprise.context.SessionScoped;
//import org.springframework.ai.chat.messages.*;
//import org.springframework.ai.openai.OpenAiChatModel;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.enjy.fit_balance.model.dto.UserAccountDto;
import ru.enjy.fit_balance.model.entity.UserAccount;
import ru.enjy.fit_balance.model.mapper.ExerciseMapper;
import ru.enjy.fit_balance.model.mapper.UserAccountMapper;
import ru.enjy.fit_balance.service.impl.UserAccountServiceImpl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
//@RequiredArgsConstructor
public class TelegramBotService extends MultiSessionTelegramBot {

    public static final String NAME = "FitBalanceFromENJYBot";
    public static final String TOKEN = "8037976874:AAFTsd7DT5Urcy3bv0B52OwY3wvIiUs-iHQ";

    private final UserAccountServiceImpl userAccountService;

//    public TelegramBotService() {
//        super(NAME, TOKEN);
//    }

    //private final OpenAiChatModel openAiChatModel;

//    @SessionScoped
//    private HashMap<Long, List<Message>> promptMessages = new HashMap<>();
//
//    public TelegramBotService(OpenAiChatModel openAiChatModel) {
//        super(NAME, TOKEN);
//        this.openAiChatModel = openAiChatModel;
//    }

    public TelegramBotService(UserAccountServiceImpl userAccountService) {
        super(NAME, TOKEN);
        this.userAccountService = userAccountService;
    }

    @Override
    public void onUpdateEventReceived(Update updateEvent) {

        if (getMessageText().equals("/start")) {
            sendTextMessageAsync("Привет! Хочешь начать тренировку?");

            sendTextMessageAsync("Давай приступим к занятиям!", Map.of("Присоединиться!", "step_1_btn"));
        }
        if (getCallbackQueryButtonKey().equals("step_1_btn")) {
            UserAccount user = new UserAccount();
            user.setChat_id(getCurrentChatId().toString());
            userAccountService.create(user);

        }

//        if (getMessageText().equals("/start")) {
//            sendTextMessageAsync("Привет! Моя роль сегодня: " + AIHelperService.getRole() + " Chat id: " + getCurrentChatId());
//        } else if (getMessageText().equals("/money")) {
//            sendTextMessageAsync("");
//        } else if (getMessageText().equals("/last")) {
//            sendTextMessageAsync(getLastSentMessage().getText());
//        } else {
//            var question = getMessageText();
//
//            SystemMessage systemMessage = new SystemMessage(AIHelperService.getFullRole());
//
//            UserMessage userMessage = new UserMessage(question);
//            List<Message> currentPrompt = promptMessages.get(getCurrentChatId()) != null ?
//                    promptMessages.get(getCurrentChatId()) : new ArrayList<>();
//
//            if (currentPrompt.isEmpty()) {
//                currentPrompt.add(systemMessage);
//            } else if (currentPrompt.size() > 100) {
//                currentPrompt.remove(1);
//            }
//
//            System.out.println("Prompt has " + currentPrompt.size() + " messages.");
//
//            currentPrompt.add(userMessage);
//
//            AssistantMessage assistantMessage = new AssistantMessage(AIHelperService.getAnswer(currentPrompt, openAiChatModel));
//
//            currentPrompt.add(assistantMessage);
//            promptMessages.put(getCurrentChatId(), currentPrompt);
//
//            sendTextMessageAsync(assistantMessage.getText());
//        }


    }



}

package ru.enjy.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.ui.Model;
import ru.enjy.service.AppInfoService;

import java.time.LocalDateTime;

@Controller
@RequiredArgsConstructor
public class MainController {

    private final AppInfoService appInfoService;

    @GetMapping("/")
    public String index(Model model) {

        Long userCount = 0L;
        Long workoutCount = 0L;
        LocalDateTime actualDate = null;

        var lastInfo = appInfoService.getLast();
        if (lastInfo != null) {
            userCount = lastInfo.getUsers();
            workoutCount = lastInfo.getWorkouts();
            actualDate = lastInfo.getCreated();
        }

        model.addAttribute("userCount", userCount);
        model.addAttribute("workoutCount", workoutCount);
        model.addAttribute("actualDate", actualDate);
        return "index";
    }
}

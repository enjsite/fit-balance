package ru.enjy.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.enjy.client.UserStatsClient;
import ru.enjy.client.WorkoutStatsClient;
import ru.enjy.model.entity.AppInfo;
import ru.enjy.service.AppInfoService;

import java.util.Objects;


@RestController
@RequestMapping(path = "/api/v1/info")
@RequiredArgsConstructor
@Slf4j
@Validated
public class AppInfoController {

    private final WorkoutStatsClient workoutClient;
    private final UserStatsClient userStatsClient;
    private final AppInfoService appInfoService;

    @Scheduled(fixedRate = 20000)
    @GetMapping({"/", ""})
    public AppInfo getAppInfo() {

        var workountCount = Long.valueOf((int) Objects.requireNonNull(workoutClient.getWorkoutsCount().getBody()));
        log.info("Get workout stats " + workountCount);

        var userCount = Long.valueOf((int) Objects.requireNonNull(userStatsClient.getUsersCount().getBody()));
        log.info("Get user stats " + userCount);

        return appInfoService.create(workountCount, userCount);
    }

}

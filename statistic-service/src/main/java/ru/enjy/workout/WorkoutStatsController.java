package ru.enjy.workout;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


@Controller
@RequestMapping(path = "/workout-stats")
@RequiredArgsConstructor
@Slf4j
@Validated
public class WorkoutStatsController {

    private final WorkoutStatsClient workoutClient;

    @GetMapping({"/", ""})
    public ResponseEntity<Object> getWorkouts() {

        log.info("Get workout stats");
        return workoutClient.getWorkouts();
    }

}

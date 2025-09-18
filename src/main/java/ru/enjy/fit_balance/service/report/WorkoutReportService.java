package ru.enjy.fit_balance.service.report;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.enjy.fit_balance.model.dto.ExerciseDto;
import ru.enjy.fit_balance.model.dto.SetDto;
import ru.enjy.fit_balance.model.dto.SupersetDto;
import ru.enjy.fit_balance.model.dto.WorkoutDto;
import ru.enjy.fit_balance.service.WorkoutService;

import java.util.List;
import java.util.Objects;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional
public class WorkoutReportService {

    private final WorkoutService workoutService;

    public String getWorkoutReport(Long workoutId) {
        WorkoutDto workoutDto = workoutService.getOne(workoutId);

        StringBuilder sb = new StringBuilder();
        sb.append("📅 *")
                .append(escapeMd(workoutDto.getTitle()))
                .append("*\n\n");

        int ssCounter = 1;
        if (workoutDto.getSupersets() != null) {
            for (SupersetDto superset : workoutDto.getSupersets()) {

                int sCounter = 1;
                if (superset.getSets() != null) {
                    if (isSupersetSingleExerciseType(superset)) {
                        for (SetDto set : superset.getSets()) {
                            if (sCounter++ == 1) {
                                sb.append(ssCounter++)
                                        .append(". ")
                                        .append("🏋️ *")
                                        .append(escapeMd(set.getExercise().getTitle()))
                                        .append("*\n");
                            }
                            sb.append("  - ")
                                    .append(formatSet(set))
                                    .append("\n");
                        }
                    } else {
                        sb.append(ssCounter++).append(". 🔁 *Суперсет ").append("*\n");

                        for (SetDto set : superset.getSets()) {
                            sb.append("  ")
                                    .append("• ")
                                    .append(escapeMd(set.getExercise().getTitle()))
                                    .append("\n");

                            sb.append("    ")
                                    .append(formatSet(set))
                                    .append("\n");
                        }
                    }
                    sb.append("\n");
                }
            }
        }

        return sb.toString();
    }

    private String formatSet(SetDto setDto) {
        StringBuilder sb = new StringBuilder();
        sb.append("`")
                .append(escapeMd(setDto.getReps().toString()))
                .append("*")
                .append(escapeMd(setDto.getWeight().toString()))
                .append("кг").append("`");
        return sb.toString();
    }

    private boolean isSupersetSingleExerciseType(SupersetDto supersetDto) {
        var sets = supersetDto.getSets();
        if (sets == null || sets.isEmpty()) {
            return true;
        }

        List<ExerciseDto> exercises = sets.stream()
                .map(SetDto::getExercise)
                .toList();

        String firstTitle = exercises.getFirst().getTitle();

        return exercises.stream()
                .map(ExerciseDto::getTitle)
                .allMatch(name -> Objects.equals(name, firstTitle));
    }

    /**
     * Экранирует спецсимволы для MarkdownV2 (Telegram)
     */
    private String escapeMd(String input) {
        return input
                // экранируем _, *, [, ], (, ), ~, `, >, #, +, =, |, {, }, !
                .replaceAll("([_\\*\\[\\]\\(\\)~`>#+=|{}!])", "\\\\$1");
    }

}

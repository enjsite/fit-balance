package ru.enjy.fit_balance.service.report;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.enjy.fit_balance.model.dto.ExerciseDto;
import ru.enjy.fit_balance.model.dto.SetDto;
import ru.enjy.fit_balance.model.dto.SupersetDto;
import ru.enjy.fit_balance.model.dto.WorkoutDto;
import ru.enjy.fit_balance.service.SupersetService;
import ru.enjy.fit_balance.service.WorkoutService;
import ru.enjy.fit_balance.service.session.WorkoutSessionService;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional
public class WorkoutReportService {

    private final SupersetService supersetService;

    private final WorkoutService workoutService;
    private final WorkoutSessionService workoutSessionService; //  потом доработать, чтобы получить id текущего упражнения

    public String getWorkoutLog(Long workoutId) {
        WorkoutDto workoutDto = workoutService.getOne(workoutId);

        StringBuilder sb = new StringBuilder();
        sb.append("📅 *")
                .append(escapeMd(workoutDto.getTitle()))
                .append("*\n\n");

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        OffsetDateTime dateStart = workoutDto.getDateStart();
        if (dateStart != null) {
            String time = dateStart.format(formatter);
            sb.append("*Начало: ").append(time).append("*\n\n");
        }

        int ssCounter = 1;
        //var workoutSets = workoutService.getFilledSetsByWorkout(workoutDto);
        var workoutSets = workoutDto.getSupersets();
        if (workoutSets != null) {
            for (SupersetDto superset : workoutSets) {

                int sCounter = 1;
                //var approaches = supersetService.getFilledSetsBySuperset(superset);
                var approaches = superset.getSets();
                if (approaches != null) {
                    if (isSupersetSingleExerciseType(superset)) {
                        sb.append(ssCounter++).append(". 🔁 *Сет ").append("*\n");

                        for (SetDto set : approaches) {
                            if (sCounter++ == 1) {
                                //sb.append(ssCounter++)
                                sb.append("  ")
                                        .append("• ")
                                        .append("🏋️ *")
                                        .append(escapeMd(set.getExercise().getTitle()))
                                        .append("*\n");
                            }
                            sb.append("  - ")
                                    .append(logFormatSet(set))
                                    .append("\n");
                        }
                    } else {
                        sb.append(ssCounter++).append(". 🔁 *Суперсет ").append("*\n");

                        for (SetDto set : approaches) {
                            sb.append("  ")
                                    .append("• ")
                                    .append(escapeMd(set.getExercise().getTitle()))
                                    .append("\n");

                            sb.append("    ")
                                    .append(logFormatSet(set))
                                    .append("\n");
                        }
                    }
                    sb.append("\n");
                }
            }
        }

        return sb.toString();
    }

    public String getWorkoutReport(Long workoutId) {
        WorkoutDto workoutDto = workoutService.getOne(workoutId);

        StringBuilder sb = new StringBuilder();
        sb.append("📅 *")
                .append(escapeMd(workoutDto.getTitle()))
                .append("*\n\n");

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        OffsetDateTime dateStart = workoutDto.getDateStart();
        if (dateStart != null) {
            String time = dateStart.format(formatter);
            sb.append("*Начало: ").append(time).append("*\n\n");
        }

        int ssCounter = 1;
        var workoutSets = workoutService.getFilledSetsByWorkout(workoutDto);
        if (workoutSets != null) {
            for (SupersetDto superset : workoutSets) {

                int sCounter = 1;
                var approaches = supersetService.getFilledSetsBySuperset(superset);
                if (approaches != null) {
                    if (isSupersetSingleExerciseType(superset)) {
                        for (SetDto set : approaches) {
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

                        for (SetDto set : approaches) {
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

        OffsetDateTime dateEnd = workoutDto.getDateEnd();
        if (dateEnd != null) {
            String time = dateEnd.format(formatter);
            sb.append("*Конец: ").append(time).append("*\n\n");
        }

        sb.append("✅ *Итоги:*\n")
                .append("• Упражнений: ").append(countExercises(workoutDto)).append("\n")
                .append("• Сетов: ").append(countSets(workoutDto)).append("\n")
                .append("• Общий тоннаж: ").append(calculateTotalWeight(workoutDto)).append(" кг\n");

        return sb.toString();
    }

    private long countExercises(WorkoutDto workout) {
        return workoutService.getFilledSetsByWorkout(workout).stream()
                .flatMap(s -> supersetService.getFilledSetsBySuperset(s).stream())
                .map(set -> set.getExercise().getTitle())
                .distinct()
                .count();
    }

    private long countSets(WorkoutDto workout) {
        return workoutService.getFilledSetsByWorkout(workout).stream()
                .mapToLong(s -> supersetService.getFilledSetsBySuperset(s).size())
                .sum();
    }

    private double calculateTotalWeight(WorkoutDto workout) {
        return workout.getSupersets().stream()
                .flatMap(s -> s.getSets().stream())
                .filter(s -> s.getWeight() != null && s.getReps() != null)
                .mapToDouble(s -> s.getWeight() * s.getReps())
                .sum();
    }

    private String logFormatSet(SetDto setDto) {
        StringBuilder sb = new StringBuilder();
        var reps = setDto.getReps() != null ? setDto.getReps().toString(): "";
        var weight = setDto.getWeight() != null ? setDto.getWeight().toString() : "";
        sb.append("`")
                .append(weight)
                .append("кг")
                .append(" * ")
                .append(reps)
                .append("`");
        return reps.isEmpty() && weight.isEmpty() ? "" : sb.toString();
    }

    private String formatSet(SetDto setDto) {
        StringBuilder sb = new StringBuilder();
        var reps = setDto.getReps() != null ? setDto.getReps().toString(): "0";
        var weight = setDto.getWeight() != null ? setDto.getWeight().toString() : "0";
        sb.append("`")
                .append(weight)
                .append("кг")
                .append(" * ")
                .append(reps)
                .append("`");
        return sb.toString();
    }

    private boolean isSupersetSingleExerciseType(SupersetDto supersetDto) {
        if (supersetDto.getSets() == null) {
            return true;
        }
        var sets = supersetDto.getSets();//.stream().filter(setDto -> setDto.getReps() != 0).toList();
        //var sets = supersetService.getFilledSetsBySuperset(supersetDto); // тут уже учитываются только не с 0 повторов

        if (sets.isEmpty()) {
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

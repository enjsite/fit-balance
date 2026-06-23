package ru.enjy.fit_balance.service;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ru.enjy.fit_balance.model.dto.SupersetDto;
import ru.enjy.fit_balance.model.dto.UserAccountDto;
import ru.enjy.fit_balance.model.dto.WorkoutDto;
import ru.enjy.fit_balance.model.entity.Workout;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public interface WorkoutService {
    Page<WorkoutDto> getAll(Pageable pageable);

    WorkoutDto getOne(Long id);

    List<WorkoutDto> getMany(List<Long> ids);

    //WorkoutDto create(WorkoutDto dto);

    Workout create(UserAccountDto userAccountDto);

    WorkoutDto patch(Long id, JsonNode patchNode) throws IOException;

    List<Long> patchMany(List<Long> ids, JsonNode patchNode) throws IOException;

    WorkoutDto delete(Long id);

    void deleteMany(List<Long> ids);

    WorkoutDto findFirstByActiveTrueAndUserChatId(String chatId);

    Optional<Workout> findInProgressWorkoutByUserId(Long userId);

    WorkoutDto finishWorkout(WorkoutDto workoutDto);

    List<SupersetDto> getFilledSetsByWorkout(WorkoutDto workoutDto);
}

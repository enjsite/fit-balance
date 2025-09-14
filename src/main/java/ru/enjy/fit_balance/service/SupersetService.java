package ru.enjy.fit_balance.service;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ru.enjy.fit_balance.model.dto.SupersetDto;
import ru.enjy.fit_balance.model.dto.WorkoutDto;

import java.io.IOException;
import java.util.List;

public interface SupersetService {
    Page<SupersetDto> getAll(Pageable pageable);

    SupersetDto getOne(Long id);

    List<SupersetDto> getMany(List<Long> ids);

    SupersetDto create(SupersetDto dto);

    SupersetDto create(WorkoutDto workoutDto);

    SupersetDto patch(Long id, JsonNode patchNode) throws IOException;

    List<Long> patchMany(List<Long> ids, JsonNode patchNode) throws IOException;

    SupersetDto delete(Long id);

    void deleteMany(List<Long> ids);

    SupersetDto findFirstByActiveAndWorkout(Long workoutId);

    SupersetDto findFirstByActiveAndWorkout(WorkoutDto workoutDto);
}

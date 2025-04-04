package ru.enjy.fit_balance.service;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ru.enjy.fit_balance.model.dto.WorkoutDto;

import java.io.IOException;
import java.util.List;

public interface WorkoutService {
    Page<WorkoutDto> getAll(Pageable pageable);

    WorkoutDto getOne(Long id);

    List<WorkoutDto> getMany(List<Long> ids);

    WorkoutDto create(WorkoutDto dto);

    WorkoutDto patch(Long id, JsonNode patchNode) throws IOException;

    List<Long> patchMany(List<Long> ids, JsonNode patchNode) throws IOException;

    WorkoutDto delete(Long id);

    void deleteMany(List<Long> ids);
}

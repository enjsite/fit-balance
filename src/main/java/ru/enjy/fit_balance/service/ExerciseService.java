package ru.enjy.fit_balance.service;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ru.enjy.fit_balance.model.dto.ExerciseDto;

import java.io.IOException;
import java.util.List;

public interface ExerciseService {
    Page<ExerciseDto> getAll(Pageable pageable);

    List<ExerciseDto> getAll();

    ExerciseDto getOne(Long id);

    List<ExerciseDto> getMany(List<Long> ids);

    ExerciseDto create(ExerciseDto dto);

    ExerciseDto patch(Long id, JsonNode patchNode) throws IOException;

    List<Long> patchMany(List<Long> ids, JsonNode patchNode) throws IOException;

    ExerciseDto delete(Long id);

    void deleteMany(List<Long> ids);
}

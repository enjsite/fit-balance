package ru.enjy.fit_balance.service;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ru.enjy.fit_balance.model.dto.SetDto;
import ru.enjy.fit_balance.model.dto.SupersetDto;
import ru.enjy.fit_balance.model.dto.WorkoutDto;
import ru.enjy.fit_balance.model.entity.Set;
import ru.enjy.fit_balance.model.entity.Superset;

import java.io.IOException;
import java.util.List;

public interface SetService {
    Page<SetDto> getAll(Pageable pageable);

    SetDto getOne(Long id);

    List<SetDto> getMany(List<Long> ids);

    SetDto create(SetDto dto);

    SetDto create(Superset superset, Long exerciseId);

    SetDto patch(Long id, JsonNode patchNode) throws IOException;

    List<Long> patchMany(List<Long> ids, JsonNode patchNode) throws IOException;

    SetDto saveWeight(SetDto setDto, Double weight);

    SetDto saveReps(SetDto setDto, Integer reps);

    SetDto save(Set set);

    SetDto delete(Long id);

    void deleteMany(List<Long> ids);

    SetDto findFirstByActiveAndSuperset(SupersetDto supersetDto);

    SetDto findFirstByActiveAndChatId(String chatId);

    //String formatSet(SetDto setDto);
}

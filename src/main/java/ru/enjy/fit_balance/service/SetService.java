package ru.enjy.fit_balance.service;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ru.enjy.fit_balance.model.dto.SetDto;

import java.io.IOException;
import java.util.List;

public interface SetService {
    Page<SetDto> getAll(Pageable pageable);

    SetDto getOne(Long id);

    List<SetDto> getMany(List<Long> ids);

    SetDto create(SetDto dto);

    SetDto patch(Long id, JsonNode patchNode) throws IOException;

    List<Long> patchMany(List<Long> ids, JsonNode patchNode) throws IOException;

    SetDto delete(Long id);

    void deleteMany(List<Long> ids);
}

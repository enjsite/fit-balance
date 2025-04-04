package ru.enjy.fit_balance.service;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ru.enjy.fit_balance.model.dto.UserDetailsDto;

import java.io.IOException;
import java.util.List;

public interface UserDetailsService {
    Page<UserDetailsDto> getAll(Pageable pageable);

    UserDetailsDto getOne(Long id);

    List<UserDetailsDto> getMany(List<Long> ids);

    UserDetailsDto create(UserDetailsDto dto);

    UserDetailsDto patch(Long id, JsonNode patchNode) throws IOException;

    List<Long> patchMany(List<Long> ids, JsonNode patchNode) throws IOException;

    UserDetailsDto delete(Long id);

    void deleteMany(List<Long> ids);
}

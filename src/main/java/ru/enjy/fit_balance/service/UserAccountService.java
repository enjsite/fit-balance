package ru.enjy.fit_balance.service;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ru.enjy.fit_balance.model.dto.UserAccountDto;
import ru.enjy.fit_balance.model.entity.UserAccount;

import java.io.IOException;
import java.util.List;

public interface UserAccountService {
    Page<UserAccountDto> getAll(Pageable pageable);

    UserAccountDto getOne(Long id);

    List<UserAccountDto> getMany(List<Long> ids);

    UserAccountDto create(UserAccountDto dto);

    UserAccountDto create(UserAccount userAccount);

    UserAccountDto patch(Long id, JsonNode patchNode) throws IOException;

    List<Long> patchMany(List<Long> ids, JsonNode patchNode) throws IOException;

    UserAccountDto delete(Long id);

    void deleteMany(List<Long> ids);
}

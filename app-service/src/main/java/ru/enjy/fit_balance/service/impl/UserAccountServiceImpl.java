package ru.enjy.fit_balance.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import ru.enjy.fit_balance.model.dto.UserAccountDto;
import ru.enjy.fit_balance.model.entity.UserAccount;
import ru.enjy.fit_balance.model.mapper.UserAccountMapper;
import ru.enjy.fit_balance.repository.UserAccountRepository;
import ru.enjy.fit_balance.service.UserAccountService;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Service
public class UserAccountServiceImpl implements UserAccountService {

    private final UserAccountMapper userAccountMapper;

    private final UserAccountRepository userAccountRepository;

    private final ObjectMapper objectMapper;

    @Override
    public Page<UserAccountDto> getAll(Pageable pageable) {
        Page<UserAccount> userAccounts = userAccountRepository.findAll(pageable);
        return userAccounts.map(userAccountMapper::toUserAccountDto);
    }

    @Override
    public Long getAllCount() {
        return (long) userAccountRepository.findAll().size();
    }

    @Override
    public UserAccountDto getOne(Long id) {
        Optional<UserAccount> userAccountOptional = userAccountRepository.findById(id);
        return userAccountMapper.toUserAccountDto(userAccountOptional.orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Entity with id `%s` not found".formatted(id))));
    }

    @Override
    public List<UserAccountDto> getMany(List<Long> ids) {
        List<UserAccount> userAccounts = userAccountRepository.findAllById(ids);
        return userAccounts.stream()
                .map(userAccountMapper::toUserAccountDto)
                .toList();
    }

    @Override
    public UserAccountDto create(UserAccountDto dto) {
        UserAccount userAccount = userAccountMapper.toEntity(dto);
        userAccount.setCreated(LocalDateTime.now());
        UserAccount resultUserAccount = userAccountRepository.save(userAccount);
        return userAccountMapper.toUserAccountDto(resultUserAccount);
    }

    @Override
    public UserAccountDto create(UserAccount userAccount) {
        userAccount.setCreated(LocalDateTime.now());
        UserAccount resultUserAccount;
        try {
            resultUserAccount = userAccountRepository.save(userAccount);
        } catch (DataIntegrityViolationException e) {
            System.out.println("User already exists.");
            resultUserAccount = userAccount;
        }

        return userAccountMapper.toUserAccountDto(resultUserAccount);
    }

    @Override
    public UserAccountDto create(String chatId) {
        UserAccount userAccount = new UserAccount();
        userAccount.setCreated(LocalDateTime.now());
        userAccount.setChatId(chatId);
        UserAccount resultUserAccount;
        try {
            resultUserAccount = userAccountRepository.save(userAccount);
        } catch (DataIntegrityViolationException e) {
            System.out.println("User already exists. " + e.getLocalizedMessage());
            resultUserAccount = userAccount;
        }

        return userAccountMapper.toUserAccountDto(resultUserAccount);
    }

    @Override
    public UserAccountDto patch(Long id, JsonNode patchNode) throws IOException {
        UserAccount userAccount = userAccountRepository.findById(id).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Entity with id `%s` not found".formatted(id)));

        UserAccountDto userAccountDto = userAccountMapper.toUserAccountDto(userAccount);
        objectMapper.readerForUpdating(userAccountDto).readValue(patchNode);
        userAccountMapper.updateWithNull(userAccountDto, userAccount);

        UserAccount resultUserAccount = userAccountRepository.save(userAccount);
        return userAccountMapper.toUserAccountDto(resultUserAccount);
    }

    @Override
    public List<Long> patchMany(List<Long> ids, JsonNode patchNode) throws IOException {
        Collection<UserAccount> userAccounts = userAccountRepository.findAllById(ids);

        for (UserAccount userAccount : userAccounts) {
            UserAccountDto userAccountDto = userAccountMapper.toUserAccountDto(userAccount);
            objectMapper.readerForUpdating(userAccountDto).readValue(patchNode);
            userAccountMapper.updateWithNull(userAccountDto, userAccount);
        }

        List<UserAccount> resultUserAccounts = userAccountRepository.saveAll(userAccounts);
        return resultUserAccounts.stream()
                .map(UserAccount::getId)
                .toList();
    }

    @Override
    public UserAccountDto delete(Long id) {
        UserAccount userAccount = userAccountRepository.findById(id).orElse(null);
        if (userAccount != null) {
            userAccountRepository.delete(userAccount);
        }
        return userAccountMapper.toUserAccountDto(userAccount);
    }

    @Override
    public void deleteMany(List<Long> ids) {
        userAccountRepository.deleteAllById(ids);
    }

    @Override
    public UserAccountDto findFirstByChatId(String chatId) {
        Optional<UserAccount> userAccountOptional = userAccountRepository.findFirstByChatIdIgnoreCase(chatId);
        return userAccountMapper.toUserAccountDto(userAccountOptional.orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Entity with chatId `%s` not found".formatted(chatId))));
    }

}

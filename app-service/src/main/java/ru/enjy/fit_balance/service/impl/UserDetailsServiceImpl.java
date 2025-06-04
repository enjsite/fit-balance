package ru.enjy.fit_balance.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import ru.enjy.fit_balance.model.dto.UserDetailsDto;
import ru.enjy.fit_balance.model.entity.UserDetails;
import ru.enjy.fit_balance.model.mapper.UserDetailsMapper;
import ru.enjy.fit_balance.repository.UserDetailsRepository;
import ru.enjy.fit_balance.service.UserDetailsService;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
@Transactional
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserDetailsMapper userDetailsMapper;

    private final UserDetailsRepository userDetailsRepository;

    private final ObjectMapper objectMapper;

    @Override
    public Page<UserDetailsDto> getAll(Pageable pageable) {
        Page<UserDetails> userDetails = userDetailsRepository.findAll(pageable);
        return userDetails.map(userDetailsMapper::toUserDetailsDto);
    }

    @Override
    public UserDetailsDto getOne(Long id) {
        Optional<UserDetails> userDetailsOptional = userDetailsRepository.findById(id);
        return userDetailsMapper.toUserDetailsDto(userDetailsOptional.orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Entity with id `%s` not found".formatted(id))));
    }

    @Override
    public List<UserDetailsDto> getMany(List<Long> ids) {
        List<UserDetails> userDetails = userDetailsRepository.findAllById(ids);
        return userDetails.stream()
                .map(userDetailsMapper::toUserDetailsDto)
                .toList();
    }

    @Override
    public UserDetailsDto create(UserDetailsDto dto) {
        UserDetails userDetails = userDetailsMapper.toEntity(dto);
        userDetails.setCreated(LocalDateTime.now());
        UserDetails resultUserDetails = userDetailsRepository.save(userDetails);
        return userDetailsMapper.toUserDetailsDto(resultUserDetails);
    }

    @Override
    public UserDetailsDto patch(Long id, JsonNode patchNode) throws IOException {
        UserDetails userDetails = userDetailsRepository.findById(id).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Entity with id `%s` not found".formatted(id)));

        UserDetailsDto userDetailsDto = userDetailsMapper.toUserDetailsDto(userDetails);
        objectMapper.readerForUpdating(userDetailsDto).readValue(patchNode);
        userDetailsMapper.updateWithNull(userDetailsDto, userDetails);

        UserDetails resultUserDetails = userDetailsRepository.save(userDetails);
        return userDetailsMapper.toUserDetailsDto(resultUserDetails);
    }

    @Override
    public List<Long> patchMany(List<Long> ids, JsonNode patchNode) throws IOException {
        Collection<UserDetails> userDetails = userDetailsRepository.findAllById(ids);

        for (UserDetails userDetail : userDetails) {
            UserDetailsDto userDetailsDto = userDetailsMapper.toUserDetailsDto(userDetail);
            objectMapper.readerForUpdating(userDetailsDto).readValue(patchNode);
            userDetailsMapper.updateWithNull(userDetailsDto, userDetail);
        }

        List<UserDetails> resultUserDetails = userDetailsRepository.saveAll(userDetails);
        return resultUserDetails.stream()
                .map(UserDetails::getId)
                .toList();
    }

    @Override
    public UserDetailsDto delete(Long id) {
        UserDetails userDetails = userDetailsRepository.findById(id).orElse(null);
        if (userDetails != null) {
            userDetailsRepository.delete(userDetails);
        }
        return userDetailsMapper.toUserDetailsDto(userDetails);
    }

    @Override
    public void deleteMany(List<Long> ids) {
        userDetailsRepository.deleteAllById(ids);
    }
}

package ru.enjy.fit_balance.controller;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedModel;
import org.springframework.web.bind.annotation.*;
import ru.enjy.fit_balance.model.dto.UserDetailsDto;
import ru.enjy.fit_balance.service.UserDetailsService;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/v1/user_details")
@RequiredArgsConstructor
public class UserDetailsController {

    private final UserDetailsService userDetailsService;

    @GetMapping
    public PagedModel<UserDetailsDto> getAll(Pageable pageable) {
        Page<UserDetailsDto> userDetailsDtos = userDetailsService.getAll(pageable);
        return new PagedModel<>(userDetailsDtos);
    }

    @GetMapping("/{id}")
    public UserDetailsDto getOne(@PathVariable Long id) {
        return userDetailsService.getOne(id);
    }

    @GetMapping("/by-ids")
    public List<UserDetailsDto> getMany(@RequestParam List<Long> ids) {
        return userDetailsService.getMany(ids);
    }

    @PostMapping
    public UserDetailsDto create(@RequestBody UserDetailsDto dto) {
        return userDetailsService.create(dto);
    }

    @PatchMapping("/{id}")
    public UserDetailsDto patch(@PathVariable Long id, @RequestBody JsonNode patchNode) throws IOException {
        return userDetailsService.patch(id, patchNode);
    }

    @PatchMapping
    public List<Long> patchMany(@RequestParam List<Long> ids, @RequestBody JsonNode patchNode) throws IOException {
        return userDetailsService.patchMany(ids, patchNode);
    }

    @DeleteMapping("/{id}")
    public UserDetailsDto delete(@PathVariable Long id) {
        return userDetailsService.delete(id);
    }

    @DeleteMapping
    public void deleteMany(@RequestParam List<Long> ids) {
        userDetailsService.deleteMany(ids);
    }
}

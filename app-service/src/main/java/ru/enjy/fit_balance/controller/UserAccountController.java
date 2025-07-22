package ru.enjy.fit_balance.controller;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedModel;
import org.springframework.web.bind.annotation.*;
import ru.enjy.fit_balance.model.dto.UserAccountDto;
import ru.enjy.fit_balance.service.UserAccountService;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
public class UserAccountController {

    private final UserAccountService userAccountService;

    @GetMapping
    public PagedModel<UserAccountDto> getAll(Pageable pageable) {
        Page<UserAccountDto> userAccountDtos = userAccountService.getAll(pageable);
        return new PagedModel<>(userAccountDtos);
    }

    @GetMapping("/count")
    public Long getAllCount() {
        return userAccountService.getAllCount();
    }

    @GetMapping("/{id}")
    public UserAccountDto getOne(@PathVariable Long id) {
        return userAccountService.getOne(id);
    }

    @GetMapping("/by-ids")
    public List<UserAccountDto> getMany(@RequestParam List<Long> ids) {
        return userAccountService.getMany(ids);
    }

    @PostMapping
    public UserAccountDto create(@RequestBody UserAccountDto dto) {
        return userAccountService.create(dto);
    }

    @PatchMapping("/{id}")
    public UserAccountDto patch(@PathVariable Long id, @RequestBody JsonNode patchNode) throws IOException {
        return userAccountService.patch(id, patchNode);
    }

    @PatchMapping
    public List<Long> patchMany(@RequestParam List<Long> ids, @RequestBody JsonNode patchNode) throws IOException {
        return userAccountService.patchMany(ids, patchNode);
    }

    @DeleteMapping("/{id}")
    public UserAccountDto delete(@PathVariable Long id) {
        return userAccountService.delete(id);
    }

    @DeleteMapping
    public void deleteMany(@RequestParam List<Long> ids) {
        userAccountService.deleteMany(ids);
    }
}

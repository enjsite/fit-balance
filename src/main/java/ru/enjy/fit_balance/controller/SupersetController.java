package ru.enjy.fit_balance.controller;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedModel;
import org.springframework.web.bind.annotation.*;
import ru.enjy.fit_balance.model.dto.SupersetDto;
import ru.enjy.fit_balance.service.SupersetService;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/v1/superset")
@RequiredArgsConstructor
public class SupersetController {

    private final SupersetService supersetService;

    @GetMapping
    public PagedModel<SupersetDto> getAll(Pageable pageable) {
        Page<SupersetDto> supersetDtos = supersetService.getAll(pageable);
        return new PagedModel<>(supersetDtos);
    }

    @GetMapping("/{id}")
    public SupersetDto getOne(@PathVariable Long id) {
        return supersetService.getOne(id);
    }

    @GetMapping("/by-ids")
    public List<SupersetDto> getMany(@RequestParam List<Long> ids) {
        return supersetService.getMany(ids);
    }

    @PostMapping
    public SupersetDto create(@RequestBody SupersetDto dto) {
        return supersetService.create(dto);
    }

    @PatchMapping("/{id}")
    public SupersetDto patch(@PathVariable Long id, @RequestBody JsonNode patchNode) throws IOException {
        return supersetService.patch(id, patchNode);
    }

    @PatchMapping
    public List<Long> patchMany(@RequestParam List<Long> ids, @RequestBody JsonNode patchNode) throws IOException {
        return supersetService.patchMany(ids, patchNode);
    }

    @DeleteMapping("/{id}")
    public SupersetDto delete(@PathVariable Long id) {
        return supersetService.delete(id);
    }

    @DeleteMapping
    public void deleteMany(@RequestParam List<Long> ids) {
        supersetService.deleteMany(ids);
    }
}

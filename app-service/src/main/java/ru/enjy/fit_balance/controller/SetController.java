package ru.enjy.fit_balance.controller;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedModel;
import org.springframework.web.bind.annotation.*;
import ru.enjy.fit_balance.model.dto.SetDto;
import ru.enjy.fit_balance.service.SetService;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/v1/set")
@RequiredArgsConstructor
public class SetController {

    private final SetService setService;

    @GetMapping
    public PagedModel<SetDto> getAll(Pageable pageable) {
        Page<SetDto> setDtos = setService.getAll(pageable);
        return new PagedModel<>(setDtos);
    }

    @GetMapping("/{id}")
    public SetDto getOne(@PathVariable Long id) {
        return setService.getOne(id);
    }

    @GetMapping("/by-ids")
    public List<SetDto> getMany(@RequestParam List<Long> ids) {
        return setService.getMany(ids);
    }

    @PostMapping
    public SetDto create(@RequestBody SetDto dto) {
        return setService.create(dto);
    }

    @PatchMapping("/{id}")
    public SetDto patch(@PathVariable Long id, @RequestBody JsonNode patchNode) throws IOException {
        return setService.patch(id, patchNode);
    }

    @PatchMapping
    public List<Long> patchMany(@RequestParam List<Long> ids, @RequestBody JsonNode patchNode) throws IOException {
        return setService.patchMany(ids, patchNode);
    }

    @DeleteMapping("/{id}")
    public SetDto delete(@PathVariable Long id) {
        return setService.delete(id);
    }

    @DeleteMapping
    public void deleteMany(@RequestParam List<Long> ids) {
        setService.deleteMany(ids);
    }
}

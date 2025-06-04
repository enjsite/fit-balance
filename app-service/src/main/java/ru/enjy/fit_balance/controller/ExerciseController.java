package ru.enjy.fit_balance.controller;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedModel;
import org.springframework.web.bind.annotation.*;
import ru.enjy.fit_balance.model.dto.ExerciseDto;
import ru.enjy.fit_balance.service.ExerciseService;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/v1/exercise")
@RequiredArgsConstructor
public class ExerciseController {

    private final ExerciseService exerciseService;

    @GetMapping
    public PagedModel<ExerciseDto> getAll(Pageable pageable) {
        Page<ExerciseDto> exerciseDtos = exerciseService.getAll(pageable);
        return new PagedModel<>(exerciseDtos);
    }

    @GetMapping("/{id}")
    public ExerciseDto getOne(@PathVariable Long id) {
        return exerciseService.getOne(id);
    }

    @GetMapping("/by-ids")
    public List<ExerciseDto> getMany(@RequestParam List<Long> ids) {
        return exerciseService.getMany(ids);
    }

    @PostMapping
    public ExerciseDto create(@RequestBody ExerciseDto dto) {
        return exerciseService.create(dto);
    }

    @PatchMapping("/{id}")
    public ExerciseDto patch(@PathVariable Long id, @RequestBody JsonNode patchNode) throws IOException {
        return exerciseService.patch(id, patchNode);
    }

    @PatchMapping
    public List<Long> patchMany(@RequestParam List<Long> ids, @RequestBody JsonNode patchNode) throws IOException {
        return exerciseService.patchMany(ids, patchNode);
    }

    @DeleteMapping("/{id}")
    public ExerciseDto delete(@PathVariable Long id) {
        return exerciseService.delete(id);
    }

    @DeleteMapping
    public void deleteMany(@RequestParam List<Long> ids) {
        exerciseService.deleteMany(ids);
    }
}

package ru.enjy.fit_balance.controller;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedModel;
import org.springframework.web.bind.annotation.*;
import ru.enjy.fit_balance.model.dto.UserAccountDto;
import ru.enjy.fit_balance.model.dto.WorkoutDto;
import ru.enjy.fit_balance.model.mapper.WorkoutMapper;
import ru.enjy.fit_balance.service.WorkoutService;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/v1/workout")
@RequiredArgsConstructor
public class WorkoutController {

    private final WorkoutService workoutService;

    private final WorkoutMapper workoutMapper;

    @GetMapping
    public PagedModel<WorkoutDto> getAll(Pageable pageable) {
        Page<WorkoutDto> workoutDtos = workoutService.getAll(pageable);
        return new PagedModel<>(workoutDtos);
    }

    @GetMapping("/{id}")
    public WorkoutDto getOne(@PathVariable Long id) {
        return workoutService.getOne(id);
    }

    @GetMapping("/by-ids")
    public List<WorkoutDto> getMany(@RequestParam List<Long> ids) {
        return workoutService.getMany(ids);
    }

    @PostMapping
    public WorkoutDto create(@RequestBody UserAccountDto dto) {
        return workoutMapper.toWorkoutDto(workoutService.create(dto));
    }

    @PatchMapping("/{id}")
    public WorkoutDto patch(@PathVariable Long id, @RequestBody JsonNode patchNode) throws IOException {
        return workoutService.patch(id, patchNode);
    }

    @PatchMapping
    public List<Long> patchMany(@RequestParam List<Long> ids, @RequestBody JsonNode patchNode) throws IOException {
        return workoutService.patchMany(ids, patchNode);
    }

    @DeleteMapping("/{id}")
    public WorkoutDto delete(@PathVariable Long id) {
        return workoutService.delete(id);
    }

    @DeleteMapping
    public void deleteMany(@RequestParam List<Long> ids) {
        workoutService.deleteMany(ids);
    }
}

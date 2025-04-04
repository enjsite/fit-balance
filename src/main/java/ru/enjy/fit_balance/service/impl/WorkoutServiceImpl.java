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
import ru.enjy.fit_balance.model.dto.WorkoutDto;
import ru.enjy.fit_balance.model.entity.Workout;
import ru.enjy.fit_balance.model.mapper.WorkoutMapper;
import ru.enjy.fit_balance.repository.WorkoutRepository;
import ru.enjy.fit_balance.service.WorkoutService;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
@Transactional
public class WorkoutServiceImpl implements WorkoutService {

    private final WorkoutMapper workoutMapper;

    private final WorkoutRepository workoutRepository;

    private final ObjectMapper objectMapper;

    @Override
    public Page<WorkoutDto> getAll(Pageable pageable) {
        Page<Workout> workouts = workoutRepository.findAll(pageable);
        return workouts.map(workoutMapper::toWorkoutDto);
    }

    @Override
    public WorkoutDto getOne(Long id) {
        Optional<Workout> workoutOptional = workoutRepository.findById(id);
        return workoutMapper.toWorkoutDto(workoutOptional.orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Entity with id `%s` not found".formatted(id))));
    }

    @Override
    public List<WorkoutDto> getMany(List<Long> ids) {
        List<Workout> workouts = workoutRepository.findAllById(ids);
        return workouts.stream()
                .map(workoutMapper::toWorkoutDto)
                .toList();
    }

    @Override
    public WorkoutDto create(WorkoutDto dto) {
        Workout workout = workoutMapper.toEntity(dto);
        if (workout.getTitle() == null) {
            workout.setTitle("Workout " + LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE));
        }
        workout.setCreated(LocalDateTime.now());
        Workout resultWorkout = workoutRepository.save(workout);
        return workoutMapper.toWorkoutDto(resultWorkout);
    }

    @Override
    public WorkoutDto patch(Long id, JsonNode patchNode) throws IOException {
        Workout workout = workoutRepository.findById(id).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Entity with id `%s` not found".formatted(id)));

        WorkoutDto workoutDto = workoutMapper.toWorkoutDto(workout);
        objectMapper.readerForUpdating(workoutDto).readValue(patchNode);
        workoutMapper.updateWithNull(workoutDto, workout);

        Workout resultWorkout = workoutRepository.save(workout);
        return workoutMapper.toWorkoutDto(resultWorkout);
    }

    @Override
    public List<Long> patchMany(List<Long> ids, JsonNode patchNode) throws IOException {
        Collection<Workout> workouts = workoutRepository.findAllById(ids);

        for (Workout workout : workouts) {
            WorkoutDto workoutDto = workoutMapper.toWorkoutDto(workout);
            objectMapper.readerForUpdating(workoutDto).readValue(patchNode);
            workoutMapper.updateWithNull(workoutDto, workout);
        }

        List<Workout> resultWorkouts = workoutRepository.saveAll(workouts);
        return resultWorkouts.stream()
                .map(Workout::getId)
                .toList();
    }

    @Override
    public WorkoutDto delete(Long id) {
        Workout workout = workoutRepository.findById(id).orElse(null);
        if (workout != null) {
            workoutRepository.delete(workout);
        }
        return workoutMapper.toWorkoutDto(workout);
    }

    @Override
    public void deleteMany(List<Long> ids) {
        workoutRepository.deleteAllById(ids);
    }
}

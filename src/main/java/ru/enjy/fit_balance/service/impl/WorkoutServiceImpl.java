package ru.enjy.fit_balance.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import ru.enjy.fit_balance.model.dto.SetDto;
import ru.enjy.fit_balance.model.dto.SupersetDto;
import ru.enjy.fit_balance.model.dto.UserAccountDto;
import ru.enjy.fit_balance.model.dto.WorkoutDto;
import ru.enjy.fit_balance.model.entity.Workout;
import ru.enjy.fit_balance.model.entity.WorkoutStatus;
import ru.enjy.fit_balance.model.mapper.UserAccountMapper;
import ru.enjy.fit_balance.model.mapper.WorkoutMapper;
import ru.enjy.fit_balance.repository.WorkoutRepository;
import ru.enjy.fit_balance.service.SetService;
import ru.enjy.fit_balance.service.SupersetService;
import ru.enjy.fit_balance.service.WorkoutService;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional
public class WorkoutServiceImpl implements WorkoutService {

    private final WorkoutMapper workoutMapper;
    private final ObjectMapper objectMapper;
    private final UserAccountMapper userAccountMapper;

    private final WorkoutRepository workoutRepository;

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
        closeAllUserActiveWorkout(dto.getUser().getId()); // завершаем все активные тренировки
        Workout workout = workoutMapper.toEntity(dto);
        if (workout.getTitle() == null) {
            workout.setTitle("Workout " + LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE));
        }
        workout.setCreated(LocalDateTime.now());
        workout.setStatus(WorkoutStatus.IN_PROGRESS);
        workout.setActive(true);
        Workout resultWorkout = workoutRepository.save(workout);
        return workoutMapper.toWorkoutDto(resultWorkout);
    }

    private void closeAllUserActiveWorkout(Long userId) {
        // искать не по активности, а по статусу
        var activeWorkouts = workoutRepository.findAllByActiveTrueAndUser_Id(userId);
        log.info("закрываем активные тренировки");
        activeWorkouts.forEach(workout -> {
            workout.setActive(false);
            workout.setStatus(WorkoutStatus.COMPLETED);
            log.info("что-то найдено и закрыто " + workout.getTitle());
            workoutRepository.save(workout);
        });
    }

    @Override
    public WorkoutDto create(UserAccountDto userAccountDto) {
        Workout workout = new Workout();
        workout.setUser(userAccountMapper.toEntity(userAccountDto));
        return create(workoutMapper.toWorkoutDto(workout));
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

    @Override
    public WorkoutDto findFirstByActiveTrueAndUserChatId(String chatId) {
        Optional<Workout> workout = workoutRepository.findFirstByActiveTrueAndUser_ChatIdLikeOrderByCreatedDesc(chatId);
        return workout.map(workoutMapper::toWorkoutDto).orElse(null);
    }

    @Override
    public WorkoutDto finishWorkout(WorkoutDto workoutDto) {
        var activeWorkout = workoutMapper.toEntity(workoutDto);
        activeWorkout.setActive(false);
        activeWorkout.setStatus(WorkoutStatus.COMPLETED);
        workoutRepository.save(activeWorkout);
        return workoutMapper.toWorkoutDto(activeWorkout);
    }

    @Override
    public List<SupersetDto> getFilledSetsByWorkout(WorkoutDto workoutDto) {
        if (workoutDto == null || workoutDto.getSupersets() == null) {
            return List.of();
        }

        return workoutDto.getSupersets().stream()
                .filter(supersetDto -> supersetDto.getSets()!= null &&
                        supersetDto.getSets().stream()
                                .anyMatch(a -> a.getReps() != null && a.getReps() > 0)
                )
                .toList();
    }
}

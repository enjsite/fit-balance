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
import ru.enjy.fit_balance.model.dto.SetDto;
import ru.enjy.fit_balance.model.dto.SupersetDto;
import ru.enjy.fit_balance.model.dto.WorkoutDto;
import ru.enjy.fit_balance.model.entity.Superset;
import ru.enjy.fit_balance.model.entity.Workout;
import ru.enjy.fit_balance.model.mapper.SupersetMapper;
import ru.enjy.fit_balance.model.mapper.WorkoutMapper;
import ru.enjy.fit_balance.repository.SupersetRepository;
import ru.enjy.fit_balance.repository.WorkoutRepository;
import ru.enjy.fit_balance.service.SetService;
import ru.enjy.fit_balance.service.SupersetService;
import ru.enjy.fit_balance.service.WorkoutService;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
@Transactional
public class SupersetServiceImpl implements SupersetService {

    private final SupersetMapper supersetMapper;
    private final ObjectMapper objectMapper;
    private final WorkoutMapper workoutMapper;

    private final SupersetRepository supersetRepository;
    private final WorkoutRepository workoutRepository;

    //private final SetService setService;

    @Override
    public Page<SupersetDto> getAll(Pageable pageable) {
        Page<Superset> supersets = supersetRepository.findAll(pageable);
        return supersets.map(supersetMapper::toSupersetDto);
    }

    @Override
    public SupersetDto getOne(Long id) {
        Optional<Superset> supersetOptional = supersetRepository.findById(id);
        return supersetMapper.toSupersetDto(supersetOptional.orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Entity with id `%s` not found".formatted(id))));
    }

    @Override
    public List<SupersetDto> getMany(List<Long> ids) {
        List<Superset> supersets = supersetRepository.findAllById(ids);
        return supersets.stream()
                .map(supersetMapper::toSupersetDto)
                .toList();
    }

    @Override
    public SupersetDto create(SupersetDto dto) {
        Superset superset = supersetMapper.toEntity(dto);
        Workout workout = workoutRepository.getReferenceById(dto.getWorkoutId());
        return create(superset, workout);
    }

    @Override
    public SupersetDto create(WorkoutDto workoutDto) {
        Superset superset = new Superset();
        Workout workout = workoutMapper.toEntity(workoutDto);
        return create(superset, workout);
    }

    private SupersetDto create(Superset superset, Workout workout) {
        superset.setWorkout(workout);
        superset.setActive(true);
        superset.setCreated(LocalDateTime.now());
        Superset resultSuperset = supersetRepository.save(superset);
        return supersetMapper.toSupersetDto(resultSuperset);
    }

    @Override
    public SupersetDto patch(Long id, JsonNode patchNode) throws IOException {
        Superset superset = supersetRepository.findById(id).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Entity with id `%s` not found".formatted(id)));

        SupersetDto supersetDto = supersetMapper.toSupersetDto(superset);
        objectMapper.readerForUpdating(supersetDto).readValue(patchNode);
        supersetMapper.updateWithNull(supersetDto, superset);

        Superset resultSuperset = supersetRepository.save(superset);
        return supersetMapper.toSupersetDto(resultSuperset);
    }

    @Override
    public List<Long> patchMany(List<Long> ids, JsonNode patchNode) throws IOException {
        Collection<Superset> supersets = supersetRepository.findAllById(ids);

        for (Superset superset : supersets) {
            SupersetDto supersetDto = supersetMapper.toSupersetDto(superset);
            objectMapper.readerForUpdating(supersetDto).readValue(patchNode);
            supersetMapper.updateWithNull(supersetDto, superset);
        }

        List<Superset> resultSupersets = supersetRepository.saveAll(supersets);
        return resultSupersets.stream()
                .map(Superset::getId)
                .toList();
    }

    @Override
    public SupersetDto delete(Long id) {
        Superset superset = supersetRepository.findById(id).orElse(null);
        if (superset != null) {
            supersetRepository.delete(superset);
        }
        return supersetMapper.toSupersetDto(superset);
    }

    @Override
    public void deleteMany(List<Long> ids) {
        supersetRepository.deleteAllById(ids);
    }

    @Override
    public SupersetDto findFirstByActiveAndWorkout(Long workoutId) {
        Optional<Superset> superset = supersetRepository.findFirstByActiveTrueAndWorkout_IdOrderByCreatedDesc(workoutId);
        return superset.map(supersetMapper::toSupersetDto).orElse(null);
    }

    @Override
    public SupersetDto findFirstByActiveAndWorkout(WorkoutDto workoutDto) {
        Optional<Superset> activeSuperset = workoutMapper.toEntity(workoutDto).getSupersets().stream()
                .filter(Superset::isActive)
                .max(Comparator.comparing(Superset::getCreated))
                .stream().findFirst();
        return activeSuperset.map(supersetMapper::toSupersetDto).orElse(null);
    }

    @Override
    public SupersetDto finishSuperset(SupersetDto dto) {
        var superset = supersetMapper.toEntity(dto);
        superset.setActive(false);
        supersetRepository.save(superset);
        return supersetMapper.toSupersetDto(superset);
    }
}

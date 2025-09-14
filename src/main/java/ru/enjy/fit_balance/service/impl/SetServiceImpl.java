package ru.enjy.fit_balance.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import ru.enjy.fit_balance.model.dto.SetDto;
import ru.enjy.fit_balance.model.dto.SupersetDto;
import ru.enjy.fit_balance.model.dto.WorkoutDto;
import ru.enjy.fit_balance.model.entity.Exercise;
import ru.enjy.fit_balance.model.entity.Set;
import ru.enjy.fit_balance.model.entity.Superset;
import ru.enjy.fit_balance.model.mapper.ExerciseMapper;
import ru.enjy.fit_balance.model.mapper.SetMapper;
import ru.enjy.fit_balance.model.mapper.SupersetMapper;
import ru.enjy.fit_balance.repository.SetRepository;
import ru.enjy.fit_balance.repository.SupersetRepository;
import ru.enjy.fit_balance.service.ExerciseService;
import ru.enjy.fit_balance.service.SetService;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class SetServiceImpl implements SetService {

    private final SetMapper setMapper;

    private final SetRepository setRepository;

    private final ObjectMapper objectMapper;

    private final SupersetRepository supersetRepository;

    private final SupersetMapper supersetMapper;

    private final ExerciseService exerciseService;

    private final ExerciseMapper exerciseMapper;

    @Override
    public Page<SetDto> getAll(Pageable pageable) {
        Page<Set> sets = setRepository.findAll(pageable);
        return sets.map(setMapper::toSetDto);
    }

    @Override
    public SetDto getOne(Long id) {
        Optional<Set> setOptional = setRepository.findById(id);
        return setMapper.toSetDto(setOptional.orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Entity with id `%s` not found".formatted(id))));
    }

    @Override
    public List<SetDto> getMany(List<Long> ids) {
        List<Set> sets = setRepository.findAllById(ids);
        return sets.stream()
                .map(setMapper::toSetDto)
                .toList();
    }

    @Override
    public SetDto create(SetDto dto) {
        Set set = setMapper.toEntity(dto);
        Superset superset = supersetRepository.getReferenceById(dto.getSupersetId());
        return create(set, superset);
    }

    @Override
    public SetDto create(SupersetDto supersetDto, Long exerciseId) {
        Set set = new Set();
        Exercise exercise = exerciseMapper.toEntity(exerciseService.getOne(exerciseId));
        set.setExercise(exercise);
        Superset superset = supersetMapper.toEntity(supersetDto);
        return create(set, superset);
    }

    private SetDto create(Set set, Superset superset) {
        set.setSuperset(superset);
        set.setCreated(LocalDateTime.now());
        set.setActive(true);
        Set resultSet = setRepository.save(set);
        return setMapper.toSetDto(resultSet);
    }

    @Override
    public SetDto patch(Long id, JsonNode patchNode) throws IOException {
        Set set = setRepository.findById(id).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Entity with id `%s` not found".formatted(id)));

        SetDto setDto = setMapper.toSetDto(set);
        objectMapper.readerForUpdating(setDto).readValue(patchNode);
        setMapper.updateWithNull(setDto, set);

        Set resultSet = setRepository.save(set);
        return setMapper.toSetDto(resultSet);
    }

    @Override
    public List<Long> patchMany(List<Long> ids, JsonNode patchNode) throws IOException {
        Collection<Set> sets = setRepository.findAllById(ids);

        for (Set set : sets) {
            SetDto setDto = setMapper.toSetDto(set);
            objectMapper.readerForUpdating(setDto).readValue(patchNode);
            setMapper.updateWithNull(setDto, set);
        }

        List<Set> resultSets = setRepository.saveAll(sets);
        return resultSets.stream()
                .map(Set::getId)
                .toList();
    }

    @Override
    public SetDto delete(Long id) {
        Set set = setRepository.findById(id).orElse(null);
        if (set != null) {
            setRepository.delete(set);
        }
        return setMapper.toSetDto(set);
    }

    @Override
    public void deleteMany(List<Long> ids) {
        setRepository.deleteAllById(ids);
    }

    @Override
    public SetDto findFirstByActiveAndSuperset(SupersetDto supersetDto) {
        Optional<Set> activeSet = supersetMapper.toEntity(supersetDto).getSets().stream()
                .filter(Set::getActive)
                .max(Comparator.comparing(Set::getCreated))
                .stream().findFirst();
        return activeSet.map(setMapper::toSetDto).orElse(null);
    }
}

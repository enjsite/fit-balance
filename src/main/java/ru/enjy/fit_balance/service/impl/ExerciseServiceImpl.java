package ru.enjy.fit_balance.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import ru.enjy.fit_balance.model.dto.ExerciseDto;
import ru.enjy.fit_balance.model.entity.Category;
import ru.enjy.fit_balance.model.entity.Exercise;
import ru.enjy.fit_balance.model.mapper.ExerciseMapper;
import ru.enjy.fit_balance.repository.CategoryRepository;
import ru.enjy.fit_balance.repository.ExerciseRepository;
import ru.enjy.fit_balance.service.ExerciseService;

import java.io.IOException;
import java.util.*;

@RequiredArgsConstructor
@Service
public class ExerciseServiceImpl implements ExerciseService {

    private final ExerciseMapper exerciseMapper;

    private final ExerciseRepository exerciseRepository;

    private final ObjectMapper objectMapper;

    private final CategoryRepository categoryRepository;

    private final CategoryServiceImpl categoryServiceImpl;

    @Override
    public Page<ExerciseDto> getAll(Pageable pageable) {
        Page<Exercise> exercises = exerciseRepository.findAll(pageable);
        return exercises.map(exerciseMapper::toExerciseDto);
    }

    @Override
    public ExerciseDto getOne(Long id) {
        Optional<Exercise> exerciseOptional = exerciseRepository.findById(id);
        return exerciseMapper.toExerciseDto(exerciseOptional.orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Entity with id `%s` not found".formatted(id))));
    }

    @Override
    public List<ExerciseDto> getMany(List<Long> ids) {
        List<Exercise> exercises = exerciseRepository.findAllById(ids);
        return exercises.stream()
                .map(exerciseMapper::toExerciseDto)
                .toList();
    }

    @Override
    public ExerciseDto create(ExerciseDto dto) {
        Exercise exercise = exerciseMapper.toEntity(dto);
        List<Category> categoryList = categoryRepository.findAllById(dto.getCategories_ids());
        exercise.setCategories(new HashSet<>(categoryList));
        Exercise resultExercise = exerciseRepository.save(exercise);
        return exerciseMapper.toExerciseDto(resultExercise);
    }

    @Override
    public ExerciseDto patch(Long id, JsonNode patchNode) throws IOException {
        Exercise exercise = exerciseRepository.findById(id).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Entity with id `%s` not found".formatted(id)));

        ExerciseDto exerciseDto = exerciseMapper.toExerciseDto(exercise);
        objectMapper.readerForUpdating(exerciseDto).readValue(patchNode);
        exerciseMapper.updateWithNull(exerciseDto, exercise);

        Exercise resultExercise = exerciseRepository.save(exercise);
        return exerciseMapper.toExerciseDto(resultExercise);
    }

    @Override
    public List<Long> patchMany(List<Long> ids, JsonNode patchNode) throws IOException {
        Collection<Exercise> exercises = exerciseRepository.findAllById(ids);

        for (Exercise exercise : exercises) {
            ExerciseDto exerciseDto = exerciseMapper.toExerciseDto(exercise);
            objectMapper.readerForUpdating(exerciseDto).readValue(patchNode);
            exerciseMapper.updateWithNull(exerciseDto, exercise);
        }

        List<Exercise> resultExercises = exerciseRepository.saveAll(exercises);
        return resultExercises.stream()
                .map(Exercise::getId)
                .toList();
    }

    @Override
    public ExerciseDto delete(Long id) {
        Exercise exercise = exerciseRepository.findById(id).orElse(null);
        if (exercise != null) {
            exerciseRepository.delete(exercise);
        }
        return exerciseMapper.toExerciseDto(exercise);
    }

    @Override
    public void deleteMany(List<Long> ids) {
        exerciseRepository.deleteAllById(ids);
    }
}

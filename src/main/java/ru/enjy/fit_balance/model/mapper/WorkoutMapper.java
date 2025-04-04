package ru.enjy.fit_balance.model.mapper;

import org.mapstruct.*;
import ru.enjy.fit_balance.model.dto.WorkoutDto;
import ru.enjy.fit_balance.model.entity.Workout;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING, uses = {UserAccountMapper.class, SupersetMapper.class})
public interface WorkoutMapper {
    Workout toEntity(WorkoutDto workoutDto);

    @AfterMapping
    default void linkSupersets(@MappingTarget Workout workout) {
        if (workout.getSupersets() != null) {
            workout.getSupersets().forEach(superset -> superset.setWorkout(workout));
        }
    }

    WorkoutDto toWorkoutDto(Workout workout);

    Workout updateWithNull(WorkoutDto workoutDto, @MappingTarget Workout workout);
}
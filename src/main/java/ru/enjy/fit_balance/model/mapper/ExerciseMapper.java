package ru.enjy.fit_balance.model.mapper;

import org.mapstruct.*;
import ru.enjy.fit_balance.model.entity.Category;
import ru.enjy.fit_balance.model.entity.Exercise;
import ru.enjy.fit_balance.model.dto.ExerciseDto;
import ru.enjy.fit_balance.model.entity.UserAccount;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING, uses = {UserAccount.class, CategoryMapper.class})
public interface ExerciseMapper {
    Exercise toEntity(ExerciseDto exerciseDto);

    ExerciseDto toExerciseDto(Exercise exercise);

    Exercise updateWithNull(ExerciseDto exerciseDto, @MappingTarget Exercise exercise);

}
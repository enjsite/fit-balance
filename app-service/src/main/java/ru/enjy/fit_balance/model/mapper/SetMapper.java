package ru.enjy.fit_balance.model.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;
import ru.enjy.fit_balance.model.dto.SetDto;
import ru.enjy.fit_balance.model.entity.Set;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING, uses = {ExerciseMapper.class})
public interface SetMapper {
    Set toEntity(SetDto setDto);

    SetDto toSetDto(Set set);

    Set updateWithNull(SetDto setDto, @MappingTarget Set set);
}
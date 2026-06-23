package ru.enjy.fit_balance.model.mapper;

import org.mapstruct.*;
import ru.enjy.fit_balance.model.dto.SetDto;
import ru.enjy.fit_balance.model.entity.Set;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING, uses = {ExerciseMapper.class})
public interface SetMapper {

    Set updateWithNull(SetDto setDto, @MappingTarget Set set);

    @Mapping(source = "supersetId", target = "superset.id")
    Set toEntity(SetDto setDto);

    @Mapping(source = "superset.id", target = "supersetId")
    SetDto toSetDto(Set set);
}
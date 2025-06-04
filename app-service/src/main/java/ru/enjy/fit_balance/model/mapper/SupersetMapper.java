package ru.enjy.fit_balance.model.mapper;

import org.mapstruct.*;
import ru.enjy.fit_balance.model.dto.SupersetDto;
import ru.enjy.fit_balance.model.entity.Superset;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING, uses = {SetMapper.class})
public interface SupersetMapper {
    Superset toEntity(SupersetDto supersetDto);

    @AfterMapping
    default void linkSets(@MappingTarget Superset superset) {
        if (superset.getSets() != null) {
            superset.getSets().forEach(set -> set.setSuperset(superset));
        }
    }

    SupersetDto toSupersetDto(Superset superset);

    Superset updateWithNull(SupersetDto supersetDto, @MappingTarget Superset superset);
}
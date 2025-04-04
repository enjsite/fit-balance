package ru.enjy.fit_balance.model.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;
import ru.enjy.fit_balance.model.dto.CategoryDto;
import ru.enjy.fit_balance.model.entity.Category;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface CategoryMapper {
    Category toEntity(CategoryDto categoryDto);

    CategoryDto toCategoryDto(Category category);

    Category updateWithNull(CategoryDto categoryDto, @MappingTarget Category category);
}
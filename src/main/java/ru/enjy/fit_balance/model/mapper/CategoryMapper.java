package ru.enjy.fit_balance.model.mapper;

import org.mapstruct.*;
import ru.enjy.fit_balance.model.dto.CategoryDto;
import ru.enjy.fit_balance.model.entity.Category;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface CategoryMapper {
    Category toEntity(CategoryDto categoryDto);

    CategoryDto toCategoryDto(Category category);

    Category updateWithNull(CategoryDto categoryDto, @MappingTarget Category category);
}
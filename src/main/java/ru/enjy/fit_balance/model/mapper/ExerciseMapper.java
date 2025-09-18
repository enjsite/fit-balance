package ru.enjy.fit_balance.model.mapper;

import org.mapstruct.*;
import ru.enjy.fit_balance.model.entity.Category;
import ru.enjy.fit_balance.model.entity.Exercise;
import ru.enjy.fit_balance.model.dto.ExerciseDto;
import ru.enjy.fit_balance.model.entity.UserAccount;

import java.util.Set;
import java.util.stream.Collectors;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING, uses = {UserAccount.class, CategoryMapper.class})
public interface ExerciseMapper {

    Exercise updateWithNull(ExerciseDto exerciseDto, @MappingTarget Exercise exercise);

    //@Mapping(source = "userId", target = "user.id")
    @Mapping(target = "user", expression = "java(toUser(exerciseDto.getUserId()))")
    Exercise toEntity(ExerciseDto exerciseDto);

    @Mapping(target = "categoryIds", expression = "java(categoriesToCategoryIds(exercise.getCategories()))")
    @Mapping(source = "user.id", target = "userId")
    ExerciseDto toExerciseDto(Exercise exercise);

    default UserAccount toUser(Long userId) {
        if (userId == null) {
            return null;
        }
        UserAccount user = new UserAccount();
        user.setId(userId);
        return user;
    }

    default Set<Long> categoriesToCategoryIds(Set<Category> categories) {
        return categories.stream().map(Category::getId).collect(Collectors.toSet());
    }
}
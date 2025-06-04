package ru.enjy.fit_balance.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Value;
import ru.enjy.fit_balance.model.entity.Exercise;

import java.util.Set;

/**
 * DTO for {@link Exercise}
 */
@Value
@JsonIgnoreProperties(ignoreUnknown = true)
public class ExerciseDto {
    Long id;
    String title;
    String description;
    UserAccountDto user;
    String image;
    //Set<CategoryDto> categories;
    Set<Long> categories_ids;
}
package ru.enjy.fit_balance.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Value;

import java.util.Set;

/**
 * DTO for {@link ru.enjy.fit_balance.model.entity.Category}
 */
@Value
@JsonIgnoreProperties(ignoreUnknown = true)
public class CategoryDto {
    Long id;
    String title;
    String description;
    Set<ExerciseDto> exercises;
}
package ru.enjy.fit_balance.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Value;

import java.time.LocalDateTime;
import java.util.Set;

/**
 * DTO for {@link ru.enjy.fit_balance.model.entity.Workout}
 */
@Value
@JsonIgnoreProperties(ignoreUnknown = true)
public class WorkoutDto {
    Long id;
    String title;
    String description;
    UserAccountDto user;
    Boolean pattern;
    LocalDateTime created;
    Set<SupersetDto> supersets;
}
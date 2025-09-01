package ru.enjy.fit_balance.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Value;
import ru.enjy.fit_balance.model.entity.Set;

import java.time.LocalDateTime;

/**
 * DTO for {@link Set}
 */
@Value
@JsonIgnoreProperties(ignoreUnknown = true)
public class SetDto {
    Long id;
    Integer number;
    ExerciseDto exercise;
    Integer reps;
    Double weight;
    Integer time;
    Boolean active;
    LocalDateTime created;
    Long supersetId;
}
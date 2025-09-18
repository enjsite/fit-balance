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
    ExerciseDto exercise;
    Integer reps;
    Double weight;
    Integer time;
    boolean active;
    LocalDateTime created;
    Long supersetId;
}
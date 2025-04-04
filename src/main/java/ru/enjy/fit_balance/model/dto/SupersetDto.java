package ru.enjy.fit_balance.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Value;
import ru.enjy.fit_balance.model.entity.Workout;

import java.util.Set;

/**
 * DTO for {@link ru.enjy.fit_balance.model.entity.Superset}
 */
@Value
@JsonIgnoreProperties(ignoreUnknown = true)
public class SupersetDto {
    Long id;
    Integer number;
    Set<SetDto> sets;
    Long workout_id;
}
package ru.enjy.fit_balance.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Value;
import ru.enjy.fit_balance.model.entity.SetApproachType;
import ru.enjy.fit_balance.model.entity.Superset;

import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO for {@link Superset}
 */
@Value
@JsonIgnoreProperties(ignoreUnknown = true)
public class SupersetDto {
    Long id;
    boolean active;
    LocalDateTime created;
    List<SetDto> sets;
    Long workoutId;
    SetApproachType type;
}
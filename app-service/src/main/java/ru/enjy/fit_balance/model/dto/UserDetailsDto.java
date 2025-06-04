package ru.enjy.fit_balance.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Value;

import java.time.LocalDateTime;

/**
 * DTO for {@link ru.enjy.fit_balance.model.entity.UserDetails}
 */
@Value
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserDetailsDto {
    Long id;
    UserAccountDto user;
    Integer age;
    Double weight;
    Double height;
    Double fat_percent;
    Double fat_visceral;
    Double muscle_mass;
    LocalDateTime created;
}
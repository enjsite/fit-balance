package ru.enjy.fit_balance.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Value;

import java.time.LocalDateTime;

/**
 * DTO for {@link ru.enjy.fit_balance.model.entity.UserAccount}
 */
@Value
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserAccountDto {
    Long id;
    String chat_id;
    String username;
    LocalDateTime created;
}
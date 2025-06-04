package ru.enjy.fit_balance.model.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "user_account")
public class UserAccount {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "chat_id", nullable = false, unique = true)
    private String chatId;

    @Column(name = "username")
    private String username;

    @Column(name = "created", nullable = false)
    private LocalDateTime created;

}
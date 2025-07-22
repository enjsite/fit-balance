package ru.enjy.model.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "app_info")
public class AppInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;


    @Column(name = "workouts", nullable = false)
    private Long workouts;

    @Column(name = "users", nullable = false)
    private Long users;

    @Column(name = "created", nullable = false)
    private LocalDateTime created;

}
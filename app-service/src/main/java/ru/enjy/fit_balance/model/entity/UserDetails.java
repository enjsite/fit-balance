package ru.enjy.fit_balance.model.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "user_details")
public class UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private UserAccount user;

    @Column(name = "age")
    private Integer age;

    @Column(name = "weight")
    private Double weight;

    @Column(name = "height")
    private Double height;

    @Column(name = "fat_percent")
    private Double fat_percent;

    @Column(name = "fat_visceral")
    private Double fat_visceral;

    @Column(name = "muscle_mass")
    private Double muscle_mass;

    @Column(name = "created", nullable = false)
    private LocalDateTime created;

}
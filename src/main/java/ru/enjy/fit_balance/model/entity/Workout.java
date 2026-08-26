package ru.enjy.fit_balance.model.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "workout")
public class Workout {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "title", nullable = false)
    private String title;

    @Lob
    @Column(name = "description")
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private UserAccount user;

    @Column(name = "pattern")
    private Boolean pattern;

    @Column(name = "active")
    private Boolean active;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private WorkoutStatus status;

    @Column(name = "created", nullable = false)
    private LocalDateTime created;

    @Column(name = "date_start")
    private OffsetDateTime dateStart;

    @Column(name = "date_end")
    private OffsetDateTime dateEnd;

    @OneToMany(mappedBy = "workout", orphanRemoval = true)
    private List<Superset> supersets = new ArrayList<>();
}
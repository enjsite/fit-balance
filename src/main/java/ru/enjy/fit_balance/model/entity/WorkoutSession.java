package ru.enjy.fit_balance.model.entity;

import jakarta.persistence.*;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@ToString(exclude = {"currentExercise", "user", "currentWorkout", "currentSuperset"})
@Table(
        name = "workout_session",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_ws_user_id", columnNames = "user_id")
        }
)
public class WorkoutSession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    /**
     * Одна сессия на пользователя
     */
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserAccount user;

    @Enumerated(EnumType.STRING)
    @Column(name = "state", nullable = false, length = 50)
    private SessionState state;

    @Column(name = "message_id")
    private Integer messageId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "current_workout_id")
    private Workout currentWorkout;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "current_superset_id")
    private Superset currentSuperset;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "current_exercise_id")
    private Exercise currentExercise;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // =========================
    // Lifecycle
    // =========================

    @PrePersist
    @PreUpdate
    public void touch() {
        this.updatedAt = LocalDateTime.now();
    }

    // =========================
    // Constructors
    // =========================

    public WorkoutSession() {
    }

    public WorkoutSession(UserAccount user) {
        this.user = user;
        this.state = SessionState.IDLE;
    }
}

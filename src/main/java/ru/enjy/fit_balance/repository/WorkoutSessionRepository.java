package ru.enjy.fit_balance.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.enjy.fit_balance.model.entity.UserAccount;
import ru.enjy.fit_balance.model.entity.WorkoutSession;

import java.util.Optional;

public interface WorkoutSessionRepository extends JpaRepository<WorkoutSession, Long> {

    Optional<WorkoutSession> findByUser(UserAccount user);

    Optional<WorkoutSession> findByUserId(Long userId);
}

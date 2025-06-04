package ru.enjy.fit_balance.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.enjy.fit_balance.model.entity.Exercise;

public interface ExerciseRepository extends JpaRepository<Exercise, Long> {
}
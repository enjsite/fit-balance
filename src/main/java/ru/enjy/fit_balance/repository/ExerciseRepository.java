package ru.enjy.fit_balance.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.enjy.fit_balance.model.entity.Exercise;

import java.util.List;
import java.util.Optional;

public interface ExerciseRepository extends JpaRepository<Exercise, Long> {
    //Exercise findByTitleIgnoreCase(String title);

    Optional<Exercise> findFirstByTitleIgnoreCase(String title);

    List<Exercise> findAllByUser_Id(Long userId);
}
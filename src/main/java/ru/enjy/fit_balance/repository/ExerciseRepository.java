package ru.enjy.fit_balance.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.enjy.fit_balance.model.entity.Exercise;

import java.util.List;
import java.util.Optional;

public interface ExerciseRepository extends JpaRepository<Exercise, Long> {
    //Exercise findByTitleIgnoreCase(String title);

    //Optional<Exercise> findFirstByTitleIgnoreCaseAndUser_Id(String title, Long userId);
    @Query("SELECT e FROM Exercise e " +
            "WHERE LOWER(e.title) = LOWER(:title) " +
            "AND (:userId IS NULL OR e.user.id = :userId) " +
            "ORDER BY CASE WHEN e.user.id IS NOT NULL THEN 0 ELSE 1 END, e.id DESC")
    Optional<Exercise> findFirstByTitleIgnoreCaseAndUserId(
            @Param("title") String title,
            @Param("userId") Long userId
    );

    List<Exercise> findAllByUser_Id(Long userId);
}
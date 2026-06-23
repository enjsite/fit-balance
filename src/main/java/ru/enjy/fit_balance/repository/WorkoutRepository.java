package ru.enjy.fit_balance.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.enjy.fit_balance.model.dto.UserAccountDto;
import ru.enjy.fit_balance.model.entity.Workout;
import ru.enjy.fit_balance.model.entity.WorkoutStatus;

import java.util.List;
import java.util.Optional;

public interface WorkoutRepository extends JpaRepository<Workout, Long> {

    Optional<Workout> findFirstByActiveTrueAndUser_ChatIdLikeOrderByCreatedDesc(String chatId);

    List<Workout> findAllByActiveTrueAndUser_Id(Long userId);

    Optional<Workout> findByStatusAndUser_Id(WorkoutStatus status, Long userId);

    boolean existsByIdAndStatus(Long id, WorkoutStatus workoutStatus);

    //boolean existsByCurrentWorkoutIdAndStatus(Long currentWorkoutId, WorkoutStatus workoutStatus);
}
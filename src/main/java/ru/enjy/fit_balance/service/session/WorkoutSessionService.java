package ru.enjy.fit_balance.service.session;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.enjy.fit_balance.model.dto.UserAccountDto;
import ru.enjy.fit_balance.model.entity.*;
import ru.enjy.fit_balance.repository.UserAccountRepository;
import ru.enjy.fit_balance.repository.WorkoutRepository;
import ru.enjy.fit_balance.repository.WorkoutSessionRepository;
import ru.enjy.fit_balance.service.UserAccountService;
import ru.enjy.fit_balance.service.WorkoutService;

import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Service
public class WorkoutSessionService {

    private final WorkoutSessionRepository sessionRepository;

    private final WorkoutRepository workoutRepository;

    private final UserAccountService userAccountService;

    private final UserAccountRepository userAccountRepository;

    private final WorkoutService workoutService;

    /**
     * Получить текущую session пользователя
     * или создать новую (если не существует)
     */
    @Transactional
    public WorkoutSession getOrCreate(Long userId, UserAccount user) {

        return sessionRepository.findByUserId(userId)
                .orElseGet(() -> createNew(user));
    }

    private WorkoutSession createNew(UserAccount user) {
        WorkoutSession session = new WorkoutSession(user);
        session.setState(SessionState.IDLE);
        return sessionRepository.save(session);
    }

    @Transactional(readOnly = true)
    public WorkoutSession getRequired(Long userId) {

        return sessionRepository.findByUserId(userId)
                .orElseThrow(() ->
                        new IllegalStateException("Session not found"));
    }

    @Transactional
    public WorkoutSession getOrRestore(Long userId, UserAccount user) {

        return sessionRepository.findByUserId(userId)
                .orElseGet(() -> restoreFromWorkout(userId, user));
    }

    private WorkoutSession restoreFromWorkout(Long userId, UserAccount user) {

        Optional<Workout> workoutOpt = workoutService.findInProgressWorkoutByUserId(userId);
        WorkoutSession session = createNew(user);

        if (workoutOpt.isEmpty()) {
            // нет тренировки → обычная новая session
            return session;
        }

        // обновляем статус сессии вместе с добавлением тренировки
        attachWorkout(session, workoutOpt.get());

        return session;
    }

    @Transactional(readOnly = true)
    public WorkoutSession getRequiredInState(Long userId, SessionState expectedState) {

        WorkoutSession session = getRequired(userId);

        if (session.getState() != expectedState) {
            throw new IllegalStateException(
                    "Invalid session state: " + session.getState()
            );
        }

        return session;
    }

    /**
     * Установить новое состояние
     */
    @Transactional
    public void updateState(WorkoutSession session, SessionState newState) {
        session.setState(newState);
        sessionRepository.save(session);
    }

    /**
     * Привязать активную тренировку
     */
    @Transactional
    public void attachWorkout(WorkoutSession session, Workout workout) {
        session.setCurrentWorkout(workout);
        session.setState(SessionState.WAITING_EXERCISE_NAME);
        sessionRepository.save(session);
    }

    /**
     * Привязать текущий superset
     */
    @Transactional
    public void attachSuperset(WorkoutSession session, Superset superset) {
        session.setCurrentSuperset(superset);
        session.setState(SessionState.WAITING_EXERCISE_NAME);
        sessionRepository.save(session);
    }

    /**
     * Привязать текущий superset
     */
    @Transactional
    public void attachExercise(WorkoutSession session, Exercise exercise) {
        session.setCurrentExercise(exercise);
        session.setState(SessionState.WAITING_WEIGHT);
        sessionRepository.save(session);
    }

    /**
     * Полностью очистить session (при завершении тренировки)
     */
    @Transactional
    public void clearSession(WorkoutSession session) {
        session.setCurrentWorkout(null);
        session.setCurrentSuperset(null);
        session.setCurrentExercise(null);
        session.setState(SessionState.IDLE);
        sessionRepository.save(session);
    }

    public boolean hasInProgressWorkoutContext(WorkoutSession session) {

        if (session.getCurrentWorkout() == null) {
            return false;
        }

        if (session.getState() == SessionState.IDLE) {
            return false;
        }

        return workoutRepository.existsByIdAndStatus(
                session.getCurrentWorkout().getId(),
                WorkoutStatus.IN_PROGRESS
        );
    }
}

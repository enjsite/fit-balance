package ru.enjy.fit_balance.service.session;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.enjy.fit_balance.model.entity.*;
import ru.enjy.fit_balance.repository.WorkoutSessionRepository;

@Slf4j
@RequiredArgsConstructor
@Service
public class WorkoutSessionService {

    private final WorkoutSessionRepository sessionRepository;

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
        sessionRepository.save(session);
    }

    /**
     * Полностью очистить session (при завершении тренировки)
     */
    @Transactional
    public void clearSession(WorkoutSession session) {
        session.setCurrentWorkout(null);
        session.setCurrentSuperset(null);
        session.setState(SessionState.IDLE);
        sessionRepository.save(session);
    }
}

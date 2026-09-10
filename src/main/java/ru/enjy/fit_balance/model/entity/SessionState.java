package ru.enjy.fit_balance.model.entity;

public enum SessionState {
    IDLE,
    WAITING_SUPERSET, // статус для того, чтобы не было ожидания ввода упражнения или чего-то подобного
    WAITING_EXERCISE_NAME,
    WAITING_WEIGHT,
    WAITING_REPS
}

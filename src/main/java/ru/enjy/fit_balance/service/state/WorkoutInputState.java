package ru.enjy.fit_balance.service.state;

public enum WorkoutInputState {
    UNREGISTERED_USER,
    NO_ACTIVE_WORKOUT,  // нет активной тренировки
    WAITING_FOR_NEW_SUPERSET,// все заполнено, можно создать новый суперсет
    WAITING_FOR_NEW_SET,// все заполнено, можно создать новый сет
    WAITING_FOR_REPS,   // ждем ввод количества повторов
    WAITING_FOR_WEIGHT  // ждем ввод рабочего веса
}

package ru.enjy.fit_balance.service.state;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.enjy.fit_balance.model.dto.SetDto;
import ru.enjy.fit_balance.model.dto.SupersetDto;
import ru.enjy.fit_balance.model.dto.WorkoutDto;
import ru.enjy.fit_balance.service.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class WorkoutStateService {

    private final UserAccountService userAccountService;
    private final WorkoutService workoutService;
    private final SupersetService supersetService;
    private final SetService setService;

    public WorkoutInputState getCurrentWorkoutInputState(String chatId) {
        if (!userAccountService.hasUserWithChatId(chatId.toString())) {
            return WorkoutInputState.UNREGISTERED_USER;
        }

        WorkoutDto activeWorkout = workoutService.findFirstByActiveTrueAndUserChatId(chatId);
        if (activeWorkout == null) {
            return WorkoutInputState.NO_ACTIVE_WORKOUT;
        }

        SupersetDto activeSuperset = supersetService.findFirstByActiveAndWorkout(activeWorkout);
        if (activeSuperset == null) {
            return WorkoutInputState.WAITING_FOR_NEW_SUPERSET;
        }

        SetDto activeSet = setService.findFirstByActiveAndSuperset(activeSuperset);

        return getSetState(activeSet);
    }

    public WorkoutInputState getSetState(SetDto set) {
        if (set == null) {
            return WorkoutInputState.WAITING_FOR_NEW_SET;
        }
        if (set.getWeight() == null) {
            return WorkoutInputState.WAITING_FOR_WEIGHT;
        }
        if (set.getReps() == null) {
            return WorkoutInputState.WAITING_FOR_REPS;
        }
        return WorkoutInputState.WAITING_FOR_NEW_SET; // почему? завершить сет?
    }

}

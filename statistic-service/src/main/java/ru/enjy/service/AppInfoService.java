package ru.enjy.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.enjy.model.entity.AppInfo;
import ru.enjy.repository.AppInfoRepository;

import java.time.LocalDateTime;

@RequiredArgsConstructor
@Service
@Slf4j
public class AppInfoService {

    private final AppInfoRepository appInfoRepository;

    public AppInfo create(Long workoutCount, Long userCount) {
        try {
            var lastInfo = getLast();
            if (lastInfo.getUsers().equals(userCount) && lastInfo.getWorkouts().equals(workoutCount)) {
                lastInfo.setCreated(LocalDateTime.now());
                return appInfoRepository.save(lastInfo);
            }
        } catch (Exception e) {
            log.info("No last Info");
        }

        var appInfo = new AppInfo();
        appInfo.setWorkouts(workoutCount);
        appInfo.setUsers(userCount);
        appInfo.setCreated(LocalDateTime.now());

        return appInfoRepository.save(appInfo);
    }

    public AppInfo getLast() {
        return appInfoRepository.findFirstByOrderByIdDesc();
    }
}

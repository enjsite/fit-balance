package ru.enjy.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.enjy.model.entity.AppInfo;

import java.util.Optional;

public interface AppInfoRepository extends JpaRepository<AppInfo, Long> {
    AppInfo findFirstByOrderByIdDesc();

}
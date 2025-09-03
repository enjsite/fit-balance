package ru.enjy.fit_balance.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.enjy.fit_balance.model.entity.Superset;

import java.util.Optional;

public interface SupersetRepository extends JpaRepository<Superset, Long> {
}
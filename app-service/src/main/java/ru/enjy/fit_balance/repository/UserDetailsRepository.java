package ru.enjy.fit_balance.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.enjy.fit_balance.model.entity.UserDetails;

public interface UserDetailsRepository extends JpaRepository<UserDetails, Long> {
}
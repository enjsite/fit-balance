package ru.enjy.fit_balance.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.enjy.fit_balance.model.entity.UserAccount;

public interface UserAccountRepository extends JpaRepository<UserAccount, Long> {
}
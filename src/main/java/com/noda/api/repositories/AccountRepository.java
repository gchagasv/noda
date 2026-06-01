package com.noda.api.repositories;

import com.noda.api.models.Account;
import com.noda.api.models.enums.AccountType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, Long> {
    Optional<Account> findByAccountNumber(String accountNumber);

    boolean existsByUserIdAndAccountType(Long userId, AccountType accountType);

    List<Account> findByAccountType(AccountType accountType);
}
package com.noda.api.services;

import com.noda.api.dtos.AccountRequestDTO;
import com.noda.api.exceptions.AccountNotFoundException;
import com.noda.api.exceptions.SameAccountTransferException;
import com.noda.api.models.Account;
import com.noda.api.models.User;
import com.noda.api.repositories.AccountRepository;
import com.noda.api.repositories.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;
    private final UserRepository userRepository;

    public Account createAccount(AccountRequestDTO dto) {
        User owner = userRepository.findById(dto.getUserID())
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + dto.getUserID()));

        Account newAccount = new Account();
        String generatedAccountNumber = java.util.UUID.randomUUID().toString().substring(0, 8);

        newAccount.setAccountNumber(generatedAccountNumber);
        newAccount.setAccountType(dto.getAccountType());
        newAccount.setBalance(BigDecimal.ZERO);
        newAccount.setUser(owner);

        return accountRepository.save(newAccount);
    }

    public List<Account> findAllAccounts() {
        return accountRepository.findAll();
    }

    @Transactional
    public void transfer(Long sourceId, Long targetId, BigDecimal amount) {
        if (sourceId.equals(targetId)) {
            throw new SameAccountTransferException("Source and target accounts must be different. ID: " + sourceId);
        }

        Account sourceAccount = accountRepository.findById(sourceId)
                .orElseThrow(() -> new AccountNotFoundException("Account not found with ID: " + sourceId));

        Account targetAccount = accountRepository.findById(targetId)
                .orElseThrow(() -> new AccountNotFoundException("Account not found with ID: " + targetId));

        sourceAccount.withdraw(amount);
        targetAccount.deposit(amount);

        accountRepository.save(sourceAccount);
        accountRepository.save(targetAccount);
    }

    @Transactional
    public Account deposit(Long id, BigDecimal amount) {
        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new AccountNotFoundException("Account not found with ID: " + id));

        account.deposit(amount);

        return accountRepository.save(account);
    }

    @Transactional
    public Account withdrawal(Long id, BigDecimal amount) {
        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new AccountNotFoundException("Account not found with ID: " + id));

        account.withdraw(amount);

        return accountRepository.save(account);
    }
}
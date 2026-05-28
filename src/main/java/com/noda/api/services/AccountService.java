package com.noda.api.services;

import com.noda.api.dtos.AccountRequestDTO;
import com.noda.api.exceptions.AccountNotFoundException;
import com.noda.api.exceptions.SameAccountTransferException;
import com.noda.api.models.Account;
import com.noda.api.models.Transaction;
import com.noda.api.models.User;
import com.noda.api.models.enums.TransactionType;
import com.noda.api.repositories.AccountRepository;
import com.noda.api.repositories.TransactionRepository;
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
    private final TransactionRepository transactionRepository;

    public Account createAccount(AccountRequestDTO dto) {
        User owner = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + dto.getUserId()));

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

        Transaction sourceReceipt = new Transaction();
        sourceReceipt.setAmount(amount);
        sourceReceipt.setAccount(sourceAccount);
        sourceReceipt.setTransactionType(TransactionType.TRANSFER);
        transactionRepository.save(sourceReceipt);

        Transaction targetReceipt = new Transaction();
        targetReceipt.setAmount(amount);
        targetReceipt.setAccount(targetAccount);
        targetReceipt.setTransactionType(TransactionType.TRANSFER);
        transactionRepository.save(targetReceipt);
    }

    @Transactional
    public Account deposit(Long id, BigDecimal amount) {
        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new AccountNotFoundException("Account not found with ID: " + id));

        account.deposit(amount);
        Account savedAccount = accountRepository.save(account);

        Transaction receipt = new Transaction();
        receipt.setAmount(amount);
        receipt.setAccount(savedAccount);
        receipt.setTransactionType(TransactionType.DEPOSIT);
        transactionRepository.save(receipt);

        return savedAccount;
    }

    @Transactional
    public Account withdrawal(Long id, BigDecimal amount) {
        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new AccountNotFoundException("Account not found with ID: " + id));

        account.withdraw(amount);
        Account savedAccount = accountRepository.save(account);

        Transaction receipt = new Transaction();
        receipt.setAmount(amount);
        receipt.setAccount(savedAccount);
        receipt.setTransactionType(TransactionType.WITHDRAWAL);
        transactionRepository.save(receipt);

        return savedAccount;
    }


    public List<Transaction> getAccountStatement(Long accountId) {
        if(!accountRepository.existsById(accountId)) {
            throw new AccountNotFoundException("Account not found with ID: " + accountId);
        }
        return transactionRepository.findByAccount_IdOrderByTimestampDesc(accountId);
    }
}
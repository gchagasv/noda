package com.noda.api.services;

import com.noda.api.dtos.AccountRequestDTO;
import com.noda.api.exceptions.AccountNotFoundException;
import com.noda.api.exceptions.SameAccountTransferException;
import com.noda.api.exceptions.UserNotFoundException;
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
        User owner = userRepository.findById(dto.userId())
                .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + dto.userId()));

        Account newAccount = new Account();
        String generatedAccountNumber = java.util.UUID.randomUUID().toString().substring(0, 8);

        newAccount.setAccountNumber(generatedAccountNumber);
        newAccount.setAccountType(dto.accountType());
        newAccount.setBalance(BigDecimal.ZERO);
        newAccount.setUser(owner);

        return accountRepository.save(newAccount);
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

     Transaction transferReceipt = new Transaction();
     transferReceipt.setSourceAccount(sourceAccount);
     transferReceipt.setDestinationAccount(targetAccount);
     transferReceipt.setAmount(amount);
     transferReceipt.setTransactionType(TransactionType.TRANSFER);
     transactionRepository.save(transferReceipt);
    }

    @Transactional
    public Account deposit(Long accountId, BigDecimal amount) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new AccountNotFoundException("Account not found with ID: " + accountId));

        account.deposit(amount);
        accountRepository.save(account);

        Transaction receipt = new Transaction();
        receipt.setAmount(amount);
        receipt.setTransactionType(TransactionType.DEPOSIT);
        receipt.setSourceAccount(null);
        receipt.setDestinationAccount(account);

        transactionRepository.save(receipt);

        return account;
    }

    @Transactional
    public Account withdrawal(Long id, BigDecimal amount) {
        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new AccountNotFoundException("Account not found with ID: " + id));

        account.withdraw(amount);
        accountRepository.save(account);

        Transaction receipt = new Transaction();
        receipt.setAmount(amount);
        receipt.setTransactionType(TransactionType.WITHDRAWAL);
        receipt.setSourceAccount(account);
        receipt.setDestinationAccount(null);
        transactionRepository.save(receipt);

        return account;
    }


    public List<Transaction> getAccountStatement(Long accountId) {
        if(!accountRepository.existsById(accountId)) {
            throw new AccountNotFoundException("Account not found with ID: " + accountId);
        }
        // new thing learned
        return transactionRepository.findBySourceAccountIdOrDestinationAccountId(accountId, accountId);
    }
}
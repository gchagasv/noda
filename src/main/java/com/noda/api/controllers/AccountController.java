package com.noda.api.controllers;

import com.noda.api.dtos.AccountResponseDTO;
import com.noda.api.dtos.TransactionRequestDTO;
import com.noda.api.dtos.TransferRequestDTO;
import com.noda.api.models.Account;
import com.noda.api.models.Transaction;
import com.noda.api.services.AccountService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/accounts")
public class AccountController {

    private final AccountService accountService;

    @PostMapping
    public ResponseEntity<AccountResponseDTO> createAccount(@Valid @RequestBody com.noda.api.dtos.AccountRequestDTO dto) {
        Account account = accountService.createAccount(dto);

        AccountResponseDTO response = new AccountResponseDTO(
                account.getId(),
                account.getAccountNumber(),
                account.getAccountType(),
                account.getBalance(),
                account.getUser().getName()
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

 @PostMapping("/transfer")
    public ResponseEntity<String> transfer(@Valid @RequestBody TransferRequestDTO dto) {
     accountService.transfer(dto.getSourceId(), dto.getTargetId(), dto.getAmount());
        return ResponseEntity.ok("Transfer successful!");
    }

    @PostMapping("/withdrawal")
    public ResponseEntity<AccountResponseDTO> withdrawal (@Valid @RequestBody TransactionRequestDTO dto) {
     Account account = accountService.withdrawal(dto.getAccountId(), dto.getAmount());
        AccountResponseDTO response = new AccountResponseDTO(
                account.getId(),
                account.getAccountNumber(),
                account.getAccountType(),
                account.getBalance(),
                account.getUser().getName()
        );

        return ResponseEntity.ok(response);
    }

    @PostMapping("/deposit")
    public ResponseEntity<AccountResponseDTO> deposit(@Valid @RequestBody TransactionRequestDTO dto) {
        Account account = accountService.deposit(dto.getAccountId(), dto.getAmount());
        AccountResponseDTO response = new AccountResponseDTO(
                account.getId(),
                account.getAccountNumber(),
                account.getAccountType(),
                account.getBalance(),
                account.getUser().getName()
        );
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{accountId}/statement")
    public ResponseEntity<List<Transaction>> getAccountStatement (@PathVariable Long accountId) {
    List<Transaction> statement = accountService.getAccountStatement(accountId);
        return ResponseEntity.ok(statement);
    }
}

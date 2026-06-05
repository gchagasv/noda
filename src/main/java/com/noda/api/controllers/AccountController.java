package com.noda.api.controllers;

import com.noda.api.dtos.*;
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
    public ResponseEntity<AccountResponseDTO> createAccount(@Valid @RequestBody AccountRequestDTO dto) {
        Account account = accountService.createAccount(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(new AccountResponseDTO(account));
    }

    @PostMapping("/transfer")
    public ResponseEntity<String> transfer(@Valid @RequestBody TransferRequestDTO dto) {
        accountService.transfer(dto.sourceId(), dto.targetId(), dto.amount());
        return ResponseEntity.ok("Transfer successful!");
    }

    @PostMapping("/withdrawal")
    public ResponseEntity<AccountResponseDTO> withdrawal(@Valid @RequestBody TransactionRequestDTO dto) {
        Account account = accountService.withdrawal(dto.accountId(), dto.amount());

        return ResponseEntity.ok(new AccountResponseDTO(account));
    }

    @PostMapping("/deposit")
    public ResponseEntity<AccountResponseDTO> deposit(@Valid @RequestBody TransactionRequestDTO dto) {
        Account account = accountService.deposit(dto.accountId(), dto.amount());
        return ResponseEntity.ok(new AccountResponseDTO(account));
    }

    @GetMapping("/{id}/statement")
    public ResponseEntity<List<TransactionResponseDTO>> getStatement(@PathVariable Long id) {
        List<Transaction> transactions = accountService.getAccountStatement(id);

        List<TransactionResponseDTO> response = transactions.stream()
                .map(TransactionResponseDTO::new)
                .toList();

        return ResponseEntity.ok(response);
    }
}
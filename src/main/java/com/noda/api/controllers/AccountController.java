package com.noda.api.controllers;

import com.noda.api.dtos.AccountResponseDTO;
import com.noda.api.dtos.TransactionRequestDTO;
import com.noda.api.dtos.TransferRequestDTO;
import com.noda.api.models.Account;
import com.noda.api.services.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/accounts")
public class AccountController {

    private final AccountService accountService;

    @PostMapping
    public ResponseEntity<AccountResponseDTO> createAccount(@RequestBody com.noda.api.dtos.AccountRequestDTO dto) {
        Account account = accountService.createAccount(dto);

        AccountResponseDTO response = new AccountResponseDTO(
                account.getId(),
                account.getAccountNumber(),
                account.getAccountType(),
                account.getBalance(),
                account.getUser().getName()
        );

        return ResponseEntity.status(201).body(response);
    }

 @PostMapping("/transfer")
    public ResponseEntity<String> transfer(@RequestBody TransferRequestDTO dto) {
     accountService.transfer(dto.getSourceId(), dto.getTargetId(), dto.getAmount());
        return ResponseEntity.ok("Transfer successful!");
    }

    @PostMapping("/withdrawal")
    public ResponseEntity<AccountResponseDTO> withdrawal (@RequestBody TransactionRequestDTO dto) {
     Account account = accountService.withdrawal(dto.getId(), dto.getAmount());
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
    public ResponseEntity<AccountResponseDTO> deposit(@RequestBody TransactionRequestDTO dto) {
        Account account = accountService.deposit(dto.getId(), dto.getAmount());
        AccountResponseDTO response = new AccountResponseDTO(
                account.getId(),
                account.getAccountNumber(),
                account.getAccountType(),
                account.getBalance(),
                account.getUser().getName()
        );
        return ResponseEntity.ok(response);
    }
}

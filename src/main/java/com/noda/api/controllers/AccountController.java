package com.noda.api.controllers;

import com.noda.api.services.AccountService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Map;

@RestController
@RequestMapping("/accounts")
public class AccountController {

    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

 @PostMapping("/transfer")
    public ResponseEntity<String> transfer(@RequestBody Map<String, Object> request) {
        Long sourceId = Long.valueOf(request.get("sourceId").toString());
        Long targetId = Long.valueOf(request.get("targetId").toString());
        BigDecimal amount = new BigDecimal(request.get("amount").toString());

        accountService.transfer(sourceId, targetId, amount);

        return ResponseEntity.ok("Transfer successful!");
    }
}

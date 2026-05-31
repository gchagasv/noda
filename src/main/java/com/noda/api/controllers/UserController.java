package com.noda.api.controllers;

import com.noda.api.exceptions.CpfAlreadyRegisteredException;
import com.noda.api.exceptions.EmailAlreadyRegisteredException;
import com.noda.api.models.User;
import com.noda.api.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping
    public ResponseEntity<User> createUser(@RequestBody User user) {
        return ResponseEntity.ok(userService.save(user));
    }
}




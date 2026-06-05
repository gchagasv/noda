package com.noda.api.controllers;

import com.noda.api.dtos.UserRequestDTO;
import com.noda.api.dtos.UserResponseDTO;
import com.noda.api.models.User;
import com.noda.api.services.UserService;
import jakarta.validation.Valid;
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
    public ResponseEntity<UserResponseDTO> createUser(@Valid @RequestBody UserRequestDTO dto) {
        User user = userService.save(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(new UserResponseDTO(user));
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDTO> getUserById(@PathVariable Long id) {
        User user = userService.findUserById(id);
        return ResponseEntity.ok(new UserResponseDTO(user));
    }
}


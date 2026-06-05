package com.noda.api.dtos;

import com.noda.api.models.User;

import java.time.LocalDate;

public record UserResponseDTO(
        Long id,
        String name,
        LocalDate birthday,
        String cpf,
        String email,
        AddressResponseDTO address
) {
    public UserResponseDTO(User user) {
        this(
                user.getId(),
                user.getName(),
                user.getBirthday(),
                user.getCpf(),
                user.getEmail(),
                user.getAddress() != null ? new AddressResponseDTO(user.getAddress()) : null
        );
    }
}
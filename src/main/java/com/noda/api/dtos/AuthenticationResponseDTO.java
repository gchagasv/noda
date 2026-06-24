package com.noda.api.dtos;

public record AuthenticationResponseDTO (
        String message,
        String token
) {
    // no token
    public AuthenticationResponseDTO(String message) {
        this(message,null);
    }
}

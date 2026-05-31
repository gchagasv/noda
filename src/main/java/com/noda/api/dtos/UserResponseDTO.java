package com.noda.api.dtos;

import java.time.LocalDate;

public record UserResponseDTO (
     Long id,
     String userName,
     LocalDate userBirthday,
     String UserCPF,
     String UserEmail
) {}

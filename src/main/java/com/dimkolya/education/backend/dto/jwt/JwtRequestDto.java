package com.dimkolya.education.backend.dto.jwt;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record JwtRequestDto(
        @NotBlank @Size(max = 20) String username,
        @NotBlank @Size(max = 64) String password
) {
}

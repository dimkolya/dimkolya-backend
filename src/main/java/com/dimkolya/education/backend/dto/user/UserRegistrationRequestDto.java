package com.dimkolya.education.backend.dto.user;

import com.dimkolya.education.backend.model.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record UserRegistrationRequestDto(
        @NotBlank
        @Size(
                min = User.MIN_USERNAME_LENGTH,
                max = User.MAX_USERNAME_LENGTH,
                message = "{user.username.size}"
        )
        @Pattern(
                regexp = "^[a-zA-Z0-9_]+$",
                message = "{user.username.pattern}"
        )
        String username,

        @NotBlank
        @Email
        String email,

        @NotBlank
        @Size(min = 8, max = 64, message = "{user.password.size}")
        @Pattern(regexp = "^(?=.*[a-zA-Z])(?=.*[0-9]).+$",
                message = "{user.password.pattern}")
        String password) {
}

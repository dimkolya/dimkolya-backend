package com.dimkolya.education.backend.dto;

import com.dimkolya.education.backend.dto.jwt.JwtRequestDto;
import com.dimkolya.education.backend.dto.user.UserRegistrationRequestDto;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class RequestValidationTest {
    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Test
    void rejectsBlankLoginCredentials() {
        assertThat(validator.validate(new JwtRequestDto("", "")))
                .extracting(violation -> violation.getPropertyPath().toString())
                .containsExactlyInAnyOrder("username", "password");
    }

    @Test
    void rejectsInvalidRegistrationFields() {
        var request = new UserRegistrationRequestDto("a!", "not-an-email", "short!");

        assertThat(validator.validate(request))
                .extracting(violation -> violation.getPropertyPath().toString())
                .contains("username", "email", "password");
    }
}

package com.dimkolya.education.backend.dto.error;

import java.time.Instant;
import java.util.Map;

public record ApiErrorDto(
        Instant timestamp,
        int status,
        String error,
        Map<String, String> fieldErrors
) {
}

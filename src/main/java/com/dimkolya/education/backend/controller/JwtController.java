package com.dimkolya.education.backend.controller;

import com.dimkolya.education.backend.dto.jwt.JwtRequestDto;
import com.dimkolya.education.backend.dto.jwt.JwtResponseDto;
import com.dimkolya.education.backend.service.JwtService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/1/jwt")
@RequiredArgsConstructor
public class JwtController {
    private final JwtService jwtService;

    @PostMapping("")
    public ResponseEntity<JwtResponseDto> login(@Valid @RequestBody JwtRequestDto requestDto) {
        return ResponseEntity.ok(jwtService.authenticate(requestDto));
    }
}

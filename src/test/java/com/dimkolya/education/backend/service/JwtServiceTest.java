package com.dimkolya.education.backend.service;

import com.dimkolya.education.backend.dto.jwt.JwtRequestDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JwtServiceTest {
    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private JwtEncoder jwtEncoder;

    @Test
    void authenticatesCredentialsBeforeReturningToken() {
        Jwt jwt = new Jwt("signed-token", Instant.now(), Instant.now().plusSeconds(60),
                java.util.Map.of("alg", "RS256"), java.util.Map.of("sub", "dima"));
        when(jwtEncoder.encode(any(JwtEncoderParameters.class))).thenReturn(jwt);
        JwtService service = new JwtService(authenticationManager, jwtEncoder);

        var response = service.authenticate(new JwtRequestDto("dima", "secret123"));

        assertThat(response.token()).isEqualTo("signed-token");
        verify(authenticationManager).authenticate(
                new UsernamePasswordAuthenticationToken("dima", "secret123")
        );
    }
}

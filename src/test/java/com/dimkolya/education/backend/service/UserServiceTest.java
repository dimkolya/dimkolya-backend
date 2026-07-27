package com.dimkolya.education.backend.service;

import com.dimkolya.education.backend.dto.user.UserRegistrationRequestDto;
import com.dimkolya.education.backend.dto.user.UserRegistrationResponseDto;
import com.dimkolya.education.backend.model.Role;
import com.dimkolya.education.backend.model.User;
import com.dimkolya.education.backend.repository.RoleRepository;
import com.dimkolya.education.backend.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private RoleRepository roleRepository;
    @Mock
    private PasswordEncoder passwordEncoder;

    private UserService userService;

    @BeforeEach
    void setUp() {
        userService = new UserService(userRepository, roleRepository, passwordEncoder);
    }

    @Test
    void registersAvailableUserWithDefaultRoleAndEncodedPassword() {
        Role role = new Role();
        role.setName("ROLE_USER");
        when(userRepository.findByUsername("dima")).thenReturn(Optional.empty());
        when(userRepository.findByEmail("dima@example.com")).thenReturn(Optional.empty());
        when(roleRepository.findByName("ROLE_USER")).thenReturn(Optional.of(role));
        when(passwordEncoder.encode("secret123")).thenReturn("encoded");

        UserRegistrationResponseDto response = userService.registerUser(
                new UserRegistrationRequestDto("dima", "dima@example.com", "secret123")
        );

        assertThat(response.isSuccess()).isTrue();
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());
        assertThat(userCaptor.getValue().getPasswordHash()).isEqualTo("encoded");
        assertThat(userCaptor.getValue().getRoles()).containsExactly(role);
    }

    @Test
    void doesNotSaveWhenUsernameOrEmailIsTaken() {
        User existing = new User();
        existing.setEmailVerified(true);
        when(userRepository.findByUsername("dima")).thenReturn(Optional.of(existing));
        when(userRepository.findByEmail("dima@example.com")).thenReturn(Optional.of(existing));

        UserRegistrationResponseDto response = userService.registerUser(
                new UserRegistrationRequestDto("dima", "dima@example.com", "secret123")
        );

        assertThat(response.isSuccess()).isFalse();
        assertThat(response.isUsernameTaken()).isTrue();
        assertThat(response.isEmailTaken()).isTrue();
        verify(userRepository, never()).save(any());
    }
}

package com.dimkolya.education.backend.service;

import com.dimkolya.education.backend.dto.user.UserDto;
import com.dimkolya.education.backend.dto.user.UserRegistrationRequestDto;
import com.dimkolya.education.backend.dto.user.UserRegistrationResponseDto;
import com.dimkolya.education.backend.model.Role;
import com.dimkolya.education.backend.model.User;
import com.dimkolya.education.backend.repository.RoleRepository;
import com.dimkolya.education.backend.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public UserDto getUserByUsername(final String username) {
        User user = userRepository.findByUsername(username).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found")
        );
        return new UserDto(
                user.getUsername(),
                user.getEmail()
        );
    }

    private boolean userExistsAbstract(User user) {
        if (user.isEmailVerified()
                || Instant.now().isBefore(user.getCreationTime().plus(3, ChronoUnit.DAYS))) {
            return true;
        }
        userRepository.delete(user);
        return false;
    }

    private boolean userExistsByEmail(String email) {
        Optional<User> user = userRepository.findByEmail(email);
        return user.filter(this::userExistsAbstract).isPresent();
    }

    private boolean userExistsByUsername(String username) {
        Optional<User> user = userRepository.findByUsername(username);
        return user.filter(this::userExistsAbstract).isPresent();
    }

    @Transactional
    public UserRegistrationResponseDto registerUser(UserRegistrationRequestDto requestDto) {
        UserRegistrationResponseDto responseDto = new UserRegistrationResponseDto();
        responseDto.setUsername(requestDto.username());
        responseDto.setEmail(requestDto.email());

        if (userExistsByUsername(requestDto.username())) {
            responseDto.setUsernameTaken(true);
        }
        if (userExistsByEmail(requestDto.email())) {
            responseDto.setEmailTaken(true);
        }

        if (!responseDto.isEmailTaken() && !responseDto.isUsernameTaken()) {
            Role userRole = roleRepository.findByName("ROLE_USER").orElseThrow(
                    () -> new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Default role not found")
            );

            User user = new User();
            user.setUsername(requestDto.username());
            user.setEmail(requestDto.email());
            user.setPasswordHash(passwordEncoder.encode(requestDto.password()));
            user.setRoles(Set.of(userRole));

            userRepository.save(user);

            responseDto.setSuccess(true);
        }

        return responseDto;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username).orElseThrow(() ->
                new UsernameNotFoundException("User not found"));
    }
}

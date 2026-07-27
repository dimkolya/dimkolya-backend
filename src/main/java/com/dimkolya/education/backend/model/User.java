package com.dimkolya.education.backend.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.Instant;
import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
@Getter
@Setter
@Table(
        name = "users",
        indexes = {
                @Index(columnList = "creationTime"),
        }
)
public class User implements UserDetails {
    public static final int MIN_USERNAME_LENGTH = 3;
    public static final int MAX_USERNAME_LENGTH = 20;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @NotBlank
    @Size(
            min = MIN_USERNAME_LENGTH,
            max = MAX_USERNAME_LENGTH,
            message = "{user.username.size}"
    )
    @Pattern(
            regexp = "^[a-zA-Z0-9_]+$",
            message = "{user.username.pattern}"
    )
    @Column(
            nullable = false,
            unique = true,
            length = 20
    )
    private String username;

    @Email
    @Column(
            nullable = false,
            unique = true,
            length = 254
    )
    private String email;

    @Column(
            nullable = false,
            length = 60
    )
    private String passwordHash;

    @Column(
            nullable = false,
            updatable = false,
            insertable = false
    )
    private Instant creationTime;

    @Column(
            nullable = false,
            updatable = false,
            insertable = false
    )
    private Instant updateTime;

    @Column(nullable = false)
    private boolean emailVerified = false;

    @Column(nullable = false)
    private boolean blocked = false;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "users_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Role> roles;

    public User() {
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles.stream()
                .map(role -> new SimpleGrantedAuthority(role.getName()))
                .collect(Collectors.toList());
    }

    @Override
    public String getPassword() {
        return passwordHash;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return !blocked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}

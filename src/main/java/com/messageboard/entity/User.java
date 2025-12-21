package com.messageboard.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Builder
@Entity
@Table(name = "users")
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
@ToString(onlyExplicitlyIncluded = true)
@Getter
@Setter
public class User implements UserDetails { // <-- IMPLEMENTS UserDetails

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    @ToString.Include
    private Long id;

    @Column(name = "user_name", nullable = false, unique = true, length = 100)
    @ToString.Include
    private String userName;

    @Column(nullable = false, unique = true)
    @ToString.Include
    private String email;

    @JsonIgnore
    @Column(nullable = false)
    private String password;

    // ADDED: Simple role field for authentication/authorization
    @Column(name = "role", nullable = false)
    @Builder.Default
    private String role = "ROLE_USER"; // Default all new users to 'ROLE_USER'

    @Column(name = "created_date", nullable = false)
    @Builder.Default
    private Timestamp createdDate = Timestamp.from(Instant.now());

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<Message> messages;

    // --- UserDetails Interface Implementation ---

    /**
     * Maps the user's role string to a Spring Security GrantedAuthority.
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // We wrap the single 'role' string (e.g., "ROLE_USER") into the required collection.
        return Collections.singletonList(new SimpleGrantedAuthority(this.role));
    }

    /**
     * Returns the field used as the principal (user identifier) in Spring Security.
     * We use the email here, as is common for JWT-based login.
     */
    @Override
    public String getUsername() {
        return this.email;
    }

    // Required by UserDetails, but we are returning true for simplicity
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    // Required by UserDetails, but we are returning true for simplicity
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    // Required by UserDetails, but we are returning true for simplicity
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    // Required by UserDetails, but we are returning true for simplicity
    @Override
    public boolean isEnabled() {
        return true;
    }
}
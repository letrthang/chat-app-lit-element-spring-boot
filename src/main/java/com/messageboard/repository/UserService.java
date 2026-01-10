package com.messageboard.repository;

import com.messageboard.dto.AuthResponse;
import com.messageboard.dto.LoginRequest;
import com.messageboard.dto.RegisterRequest;
import com.messageboard.entity.User;
import com.messageboard.config.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    private final UserRepository userRepository;

    public UserService(UserRepository repository) {
        this.userRepository = repository;
    }

    public List<User> findAll() {
        return userRepository.findAll();
    }

    public Page<User> findAllPageable(Pageable pageable){
        return userRepository.findAll(pageable);
    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email).orElse(null);
    }
    public void delete(User entity) {
        userRepository.delete(entity);
    }
    public User saveAndUpdate(User entity) {
        return userRepository.save(entity);
    }
    public Optional<User> findByName(@Param("name") String name){
        return userRepository.findByName(name);
    }

    /**
     * Validates uniqueness; persists encoded user; returns authenticated response
     */
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByUserName(request.getUserName())) {
            throw new RuntimeException("Username already exists");
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        User user = new User();
        user.setUserName(request.getUserName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        String token = jwtUtil.generateToken(user.getEmail());
        String refreshToken = jwtUtil.generateRefreshToken(user.getEmail());
        user.setRefreshToken(refreshToken);

        user = userRepository.save(user);

        return new AuthResponse(user.getId(), user.getUsername(), user.getEmail(), token, refreshToken);
    }

    /**
     * Authenticates credentials; generates token; returns user session
     */
    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByUserName(request.getUserName())
                .orElseThrow(() -> new RuntimeException("Invalid credentials"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid credentials");
        }

        String token = jwtUtil.generateToken(user.getEmail());
        String refreshToken = jwtUtil.generateRefreshToken(user.getEmail());
        user.setRefreshToken(refreshToken);
        userRepository.save(user);

        return new AuthResponse(user.getId(), user.getUsername(), user.getEmail(), token, refreshToken);
    }

    public User getCurrentUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    /**
     * Refreshes access token using refresh token
     */
    public AuthResponse refreshToken(String refreshToken) {
        try {
            // Extract email from refresh token
            String email = jwtUtil.extractEmail(refreshToken);

            // Find user by email
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            // Validate refresh token matches stored token and is valid
            if (!refreshToken.equals(user.getRefreshToken())) {
                throw new RuntimeException("Invalid refresh token");
            }

            if (!jwtUtil.validateRefreshToken(refreshToken, email)) {
                throw new RuntimeException("Refresh token expired or invalid");
            }

            // Generate new access token (keep same refresh token)
            String newAccessToken = jwtUtil.generateToken(email);

            return new AuthResponse(user.getId(), user.getUsername(), user.getEmail(), newAccessToken, refreshToken);
        } catch (Exception e) {
            throw new RuntimeException("Invalid refresh token: " + e.getMessage());
        }
    }

    /**
     * Logout - invalidates refresh token
     */
    public void logout(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setRefreshToken(null);
        userRepository.save(user);
    }

}

package com.messageboard.repository;

import com.messageboard.dto.AuthResponse;
import com.messageboard.dto.LoginRequest;
import com.messageboard.dto.RegisterRequest;
import com.messageboard.entity.User;
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

        user = userRepository.save(user);

        // In production, generate a proper JWT token
        String token = "dummy-token-" + user.getId();

        return new AuthResponse(user.getId(), user.getUserName(), user.getEmail(), token);
    }

    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByUserName(request.getUserName())
                .orElseThrow(() -> new RuntimeException("Invalid credentials"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid credentials");
        }

        // In production, generate a proper JWT token
        String token = "dummy-token-" + user.getId();

        return new AuthResponse(user.getId(), user.getUserName(), user.getEmail(), token);
    }

    public User getCurrentUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

}

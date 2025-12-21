package com.messageboard.dto;

import com.messageboard.entity.User;
import org.springframework.stereotype.Service;

@Service
public class UserMapper {

    /**
     * Converts a UserDTO to a User entity for persistence (Registration).
     * We initialize ID, createdDate, messages to null/default, and set the role to default.
     * @param dto The DTO containing registration data.
     * @return The User entity.
     */
    public User toEntity(UserDTO dto) {
        // The User entity's constructor signature changed (ID, userName, email, password, role, createdDate, messages)
        // Note: The role is set to the default "ROLE_USER" here, mirroring the entity's @Builder.Default value.
        return User.builder()
                .userName(dto.getUserName())
                .email(dto.getEmail())
                .password(dto.getPassword()) // IMPORTANT: Password should be hashed in the service layer, not here.
                .role("ROLE_USER") // Explicitly set the default role
                .build();
    }

    /**
     * Converts a User entity to a UserDTO for external response (e.g., after login).
     * @param entity The User entity.
     * @return The UserDTO.
     */
    public UserDTO toDTO(User entity) {
        return new UserDTO(
                entity.getEmail(),
                // Use the correct getter for the name field
                entity.getUsername(),
                // DO NOT expose the password in the DTO used for responses!
                null
        );
    }
}
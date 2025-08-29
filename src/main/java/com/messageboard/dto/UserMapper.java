package com.messageboard.dto;

import com.messageboard.entity.User;
import org.springframework.stereotype.Service;

@Service
public class UserMapper {
    public User toEntity(UserDTO dto) {
        return new User(null, dto.getEmail(), dto.getName(), dto.getPassword(), null, null);
    }

    public UserDTO toDTO(User entity) {
        return new UserDTO(
                entity.getEmail(),
                entity.getUserName(),
                entity.getPassword()

        );
    }
}

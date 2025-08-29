package com.messageboard.dto;

import com.messageboard.entity.User;
import lombok.*;

import java.sql.Timestamp;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class UserResponse {
    private Long id;
    private String userName;
    private String email;
    private Timestamp createdDate;

    // Constructor from Entity
    public UserResponse(User user) {
        this.id = user.getId();
        this.userName = user.getUserName();
        this.email = user.getEmail();
        this.createdDate = user.getCreatedDate();
    }

    // Static factory method
    public static UserResponse from(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .userName(user.getUserName())
                .email(user.getEmail())
                .createdDate(user.getCreatedDate())
                .build();
    }
}

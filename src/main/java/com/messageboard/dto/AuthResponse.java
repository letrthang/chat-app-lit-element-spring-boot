package com.messageboard.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class AuthResponse {
    private Long id;
    private String userName;
    private String email;
    private String token;
}
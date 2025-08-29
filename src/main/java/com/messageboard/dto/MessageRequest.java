package com.messageboard.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class MessageRequest {

    @NotBlank(message = "Message content is required")
    @Size(min = 3, max = 200, message = "Message must be between 3 and 200 characters")
    private String content;

    private Long parentMessageId;
}

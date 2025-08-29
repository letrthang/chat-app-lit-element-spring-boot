package com.messageboard.dto;

import lombok.*;
import java.time.LocalDateTime;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class ApiErrorResponse {

    @Builder.Default
    private LocalDateTime timestamp = LocalDateTime.now();

    private int status;
    private String error;
    private String message;
    private String path;
    private Map<String, String> validationErrors;

    // Convenience factory methods
    public static ApiErrorResponse badRequest(String message) {
        return ApiErrorResponse.builder()
                .status(400)
                .error("Bad Request")
                .message(message)
                .build();
    }

    public static ApiErrorResponse notFound(String message) {
        return ApiErrorResponse.builder()
                .status(404)
                .error("Not Found")
                .message(message)
                .build();
    }

    public static ApiErrorResponse validationError(Map<String, String> errors) {
        return ApiErrorResponse.builder()
                .status(400)
                .error("Validation Failed")
                .message("Invalid input parameters")
                .validationErrors(errors)
                .build();
    }
}

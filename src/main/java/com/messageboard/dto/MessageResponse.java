package com.messageboard.dto;


import com.messageboard.entity.Message;
import lombok.*;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class MessageResponse {
    private Long id;
    private String content;
    private String userName;
    private Long userId;
    private Timestamp createdAt;
    private String messageType;
    private Long parentMessageId;
    private List<MessageResponse> replies;
    private int replyCount;

    // Constructor from Entity
    public MessageResponse(Message message) {
        this.id = message.getId();
        this.content = message.getContent();
        this.userName = message.getUser().getUserName();
        this.userId = message.getUser().getId();
        this.createdAt = message.getCreatedAt();
        this.messageType = message.getMessageType().toString();
        this.parentMessageId = message.getParentMessage() != null ?
                message.getParentMessage().getId() : null;

        if (message.getReplies() != null) {
            this.replies = message.getReplies().stream()
                    .map(MessageResponse::new)
                    .collect(Collectors.toList());
            this.replyCount = message.getReplies().size();
        } else {
            this.replyCount = 0;
        }
    }

    // Static factory method (alternative approach)
    public static MessageResponse from(Message message) {
        return new MessageResponse(message);
    }
}

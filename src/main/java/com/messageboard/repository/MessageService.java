package com.messageboard.repository;

import com.messageboard.dto.MessageRequest;
import com.messageboard.dto.MessageResponse;
import com.messageboard.entity.Message;
import com.messageboard.entity.MessageType;
import com.messageboard.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class MessageService {

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private UserService userService;

    public MessageResponse createMessage(MessageRequest request, Long userId) {
        User user = userService.getCurrentUser(userId);

        Message message = new Message();
        message.setUser(user);
        message.setContent(request.getContent());

        if (request.getParentMessageId() != null) {
            Message parentMessage = messageRepository.findById(request.getParentMessageId())
                    .orElseThrow(() -> new RuntimeException("Parent message not found"));
            message.setParentMessage(parentMessage);
            message.setMessageType(MessageType.COMMENT);
        } else {
            message.setMessageType(MessageType.POST);
        }

        message = messageRepository.save(message);
        return new MessageResponse(message);
    }

    public List<MessageResponse> getAllPosts() {
        return messageRepository.findAllPosts().stream()
                .map(MessageResponse::new)
                .collect(Collectors.toList());
    }

    public MessageResponse getMessageWithReplies(Long messageId) {
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new RuntimeException("Message not found"));
        return new MessageResponse(message);
    }

    public List<MessageResponse> getReplies(Long messageId) {
        return messageRepository.findByParentMessageIdOrderByCreatedAt(messageId).stream()
                .map(MessageResponse::new)
                .collect(Collectors.toList());
    }
}

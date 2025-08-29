package com.messageboard.api;

import com.messageboard.dto.MessageRequest;
import com.messageboard.dto.MessageResponse;
import com.messageboard.repository.MessageService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@Tag(name = "MessageController")
@RestController
@RequestMapping("/api/messages")
@CrossOrigin(origins = "*")
public class MessageController {

    @Autowired
    private MessageService messageService;

    @PostMapping
    public ResponseEntity<MessageResponse> createMessage(
            @Valid @RequestBody MessageRequest request,
            @RequestHeader("User-Id") Long userId) {
        MessageResponse response = messageService.createMessage(request, userId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/posts")
    public ResponseEntity<List<MessageResponse>> getAllPosts() {
        List<MessageResponse> posts = messageService.getAllPosts();
        return ResponseEntity.ok(posts);
    }

    @GetMapping("/{messageId}")
    public ResponseEntity<MessageResponse> getMessageWithReplies(@PathVariable Long messageId) {
        MessageResponse response = messageService.getMessageWithReplies(messageId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{messageId}/replies")
    public ResponseEntity<List<MessageResponse>> getReplies(@PathVariable Long messageId) {
        List<MessageResponse> replies = messageService.getReplies(messageId);
        return ResponseEntity.ok(replies);
    }
}

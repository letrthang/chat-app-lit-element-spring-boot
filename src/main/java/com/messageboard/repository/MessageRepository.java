package com.messageboard.repository;

import com.messageboard.entity.Message;
import com.messageboard.entity.MessageType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {
    List<Message> findByMessageTypeAndParentMessageIsNullOrderByCreatedAtDesc(MessageType messageType);

    List<Message> findByParentMessageIdOrderByCreatedAt(Long parentMessageId);

    @Query("SELECT m FROM Message m WHERE m.messageType = 'POST' AND m.parentMessage IS NULL ORDER BY m.createdAt DESC")
    List<Message> findAllPosts();

    @Query("SELECT COUNT(m) FROM Message m WHERE m.parentMessage.id = :postId")
    Long countCommentsByPostId(Long postId);
}
package com.messageboard.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "messages")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(onlyExplicitlyIncluded = true)
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    @ToString.Include
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false)
    @ToString.Include
    private User user;

    @Size(min = 3, max = 200, message = "Message must be between 3 and 200 characters")
    @Column(nullable = false, length = 200)
    @ToString.Include
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_message_id")
    @JsonIgnore
    private Message parentMessage;

    @OneToMany(mappedBy = "parentMessage", cascade = CascadeType.ALL)
    @Builder.Default
    private List<Message> replies = new ArrayList<>();

    @Column(name = "message_type", nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    @Builder.Default
    @ToString.Include
    private MessageType messageType = MessageType.POST;

    @Column(name = "created_at", nullable = false)
    @Builder.Default
    private Timestamp createdAt = Timestamp.from(Instant.now());

    @Column(name = "updated_at", nullable = false)
    @Builder.Default
    private Timestamp updatedAt = Timestamp.from(Instant.now());

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = Timestamp.from(Instant.now());
    }
}

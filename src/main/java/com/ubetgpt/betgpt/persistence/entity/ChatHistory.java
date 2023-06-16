package com.ubetgpt.betgpt.persistence.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "chat_history")
public class ChatHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private User user;
    @Column(length = 5000)
    private String userMessage;
    @Column(length = 5000)
    private String gptMessage;
    private LocalDateTime created;
}

package com.ubetgpt.betgpt.model.chat;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;

@AllArgsConstructor
@Data
@ToString
public class ChatMessage {
    private String role;
    private String content;
}

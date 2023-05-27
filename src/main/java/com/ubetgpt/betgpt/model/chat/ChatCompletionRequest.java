package com.ubetgpt.betgpt.model.chat;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;
@AllArgsConstructor
@NoArgsConstructor
@Data
@ToString
public class ChatCompletionRequest {
    private String model;
    private List<ChatMessage> messages;
}


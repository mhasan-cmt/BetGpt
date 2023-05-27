package com.ubetgpt.betgpt.controller;

import com.ubetgpt.betgpt.model.chat.ChatCompletionRequest;
import com.ubetgpt.betgpt.model.chat.ChatResponse;
import com.ubetgpt.betgpt.service.ChatCompletionService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
public class ChatCompletionController {
    private final ChatCompletionService chatCompletionService;

    @PostMapping("/chat/completions")
    public ResponseEntity<ChatResponse> createChatCompletion(@RequestBody ChatCompletionRequest request) {
        ChatResponse response = chatCompletionService.createChatCompletion(request);
        return ResponseEntity.ok(response);
    }
}


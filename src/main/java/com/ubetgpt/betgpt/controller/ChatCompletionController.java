package com.ubetgpt.betgpt.controller;

import com.ubetgpt.betgpt.config.OpenAIConfiguration;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.CompletableFuture;

@RestController
@AllArgsConstructor
public class ChatCompletionController {
    private final OpenAIConfiguration openAIConfiguration;

    @PostMapping("/chat/completions")
    @Async
    @CrossOrigin(origins = "http://localhost:8080")
    public CompletableFuture<ResponseEntity<String>> createChatCompletion(@RequestBody String requestBody) {
        String apiKey = openAIConfiguration.getApiKey();
        String baseURL = openAIConfiguration.getBaseURL() + "/chat/completions";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey);

        RestTemplate restTemplate = new RestTemplate();
        HttpEntity<String> request = new HttpEntity<>(requestBody, headers);
        ResponseEntity<String> response = restTemplate.postForEntity(baseURL, request, String.class);
        return CompletableFuture.completedFuture(response);
    }
}

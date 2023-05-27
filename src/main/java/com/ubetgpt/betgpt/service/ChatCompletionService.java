package com.ubetgpt.betgpt.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ubetgpt.betgpt.model.chat.ChatCompletionRequest;
import com.ubetgpt.betgpt.model.chat.ChatResponse;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class ChatCompletionService {
    @Value("${openai.api-key}")
    private String apiKey;
    @Value("${openai.base-url}")
    private String baseURL;
    @Autowired
    private ObjectMapper objectMapper;

    public ChatResponse createChatCompletion(ChatCompletionRequest request) {
        String baseURL = this.baseURL + "/chat/completions";

        // Set headers with API key
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey);

        // Make the API request
        RestTemplate restTemplate = new RestTemplate();
        HttpEntity<ChatCompletionRequest> requestEntity = new HttpEntity<>(request, headers);
        ResponseEntity<String> responseEntity = restTemplate.postForEntity(baseURL, requestEntity, String.class);

        // Retrieve the response body
        ChatResponse response = null;
        try {
            response = objectMapper.readValue(responseEntity.getBody(), ChatResponse.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        return response;
    }
}


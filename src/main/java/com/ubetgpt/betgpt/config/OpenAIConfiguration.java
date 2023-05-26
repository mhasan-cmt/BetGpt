package com.ubetgpt.betgpt.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenAIConfiguration {
    @Value("${openai.api-key}")
    private String apiKey;

    @Value("${openai.base-url}")
    private String baseURL;

    public String getApiKey() {
        return apiKey;
    }

    public String getBaseURL() {
        return baseURL;
    }
}

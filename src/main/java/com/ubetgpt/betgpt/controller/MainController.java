package com.ubetgpt.betgpt.controller;

import com.ubetgpt.betgpt.model.chat.ChatCompletionRequest;
import com.ubetgpt.betgpt.model.chat.ChatMessage;
import com.ubetgpt.betgpt.model.chat.ChatResponse;
import com.ubetgpt.betgpt.service.ChatCompletionService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/")
@AllArgsConstructor
public class MainController {
    private final ChatCompletionService chatCompletionService;
    private final String model = "gpt-3.5-turbo";
    @GetMapping
    public String homePage(){
        return "index";
    }

    @GetMapping("/chat")
    public String chatPage(){
        ChatCompletionRequest request = new ChatCompletionRequest();
        request.setModel(model);

        List<ChatMessage> messages = new ArrayList<>();
        messages.add(new ChatMessage("user", "How can i learn java!"));
        request.setMessages(messages);

        ChatResponse response = chatCompletionService.createChatCompletion(request);
        System.out.println(response);
        return "index";
    }
}

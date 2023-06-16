package com.ubetgpt.betgpt.service;

import com.ubetgpt.betgpt.persistence.entity.ChatHistory;
import com.ubetgpt.betgpt.persistence.repository.ChatHistoryRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class ChatHistoryService {
    private final ChatHistoryRepository chatHistoryRepository;
    public void saveChatHistory(ChatHistory chatHistory) {
        chatHistoryRepository.save(chatHistory);
    }

    public List<ChatHistory> getChatHistoryByUser(Long userId) {
        return chatHistoryRepository.findByUserId(userId);
    }
}

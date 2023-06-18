package com.ubetgpt.betgpt.service;

import com.ubetgpt.betgpt.persistence.entity.ChatHistory;
import com.ubetgpt.betgpt.persistence.repository.ChatHistoryRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
@Transactional
public class ChatHistoryService {
    private final ChatHistoryRepository chatHistoryRepository;
    public void saveChatHistory(ChatHistory chatHistory) {
        chatHistoryRepository.save(chatHistory);
    }

    public List<ChatHistory> getChatHistoryByUser(Long userId) {
        return chatHistoryRepository.findByUserId(userId);
    }

    public void deleteChatHistoryByUser(Long id) {
        chatHistoryRepository.deleteByUserId(id);
    }
}

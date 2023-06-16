package com.ubetgpt.betgpt.persistence.repository;

import com.ubetgpt.betgpt.persistence.entity.ChatHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatHistoryRepository extends JpaRepository<ChatHistory, Long> {
    List<ChatHistory> findByUserId(Long userId);
}

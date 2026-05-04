package com.renewable.ai.repository;

import com.renewable.ai.entity.AiChatSession;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface AiChatSessionRepository extends JpaRepository<AiChatSession, String> {
    List<AiChatSession> findByUserIdOrderByCreatedAtDesc(Long userId);
}

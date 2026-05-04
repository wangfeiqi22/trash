package com.renewable.ai.service;

import com.renewable.ai.entity.AiChatMessage;
import com.renewable.ai.entity.AiChatSession;
import com.renewable.ai.repository.AiChatMessageRepository;
import com.renewable.ai.repository.AiChatSessionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class AiChatService {

    @Autowired
    private AiChatSessionRepository sessionRepository;

    @Autowired
    private AiChatMessageRepository messageRepository;

    @Autowired
    private KnowledgeBaseService knowledgeBaseService;

    public AiChatSession createSession(Long userId) {
        AiChatSession session = new AiChatSession();
        session.setId(UUID.randomUUID().toString());
        session.setUserId(userId);
        return sessionRepository.save(session);
    }

    public List<AiChatSession> getUserSessions(Long userId) {
        return sessionRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    public AiChatMessage sendMessage(String sessionId, String content) {
        // 1. Save User Message
        AiChatMessage userMsg = new AiChatMessage();
        userMsg.setSessionId(sessionId);
        userMsg.setSenderType("user");
        userMsg.setContent(content);
        messageRepository.save(userMsg);

        // 2. Mock AI Response (In real world, call Rasa/OpenAI)
        String aiResponseText = generateMockAiResponse(content);
        
        AiChatMessage aiMsg = new AiChatMessage();
        aiMsg.setSessionId(sessionId);
        aiMsg.setSenderType("ai");
        aiMsg.setContent(aiResponseText);
        return messageRepository.save(aiMsg);
    }

    public List<AiChatMessage> getHistory(String sessionId) {
        return messageRepository.findBySessionIdOrderByCreatedAtAsc(sessionId);
    }

    private String generateMockAiResponse(String input) {
        // 1. Check Knowledge Base first
        String kbAnswer = knowledgeBaseService.findAnswer(input);
        if (kbAnswer != null) {
            return kbAnswer;
        }

        // 2. Fallback to hardcoded logic
        String lowerInput = input.toLowerCase();
        if (lowerInput.contains("你好") || lowerInput.contains("hello")) {
            return "您好！我是智慧垃圾清运助手，请问有什么可以帮您？";
        } else if (lowerInput.contains("价格") || lowerInput.contains("费用")) {
            return "我们的清运费用根据垃圾类型和重量计算，生活垃圾约 50元/吨，建筑垃圾约 80元/吨。";
        } else if (lowerInput.contains("下单") || lowerInput.contains("预约")) {
            return "您可以点击底部的“创建清运单”按钮进行预约。";
        } else if (lowerInput.contains("投诉")) {
            return "很抱歉给您带来不便，已为您转接人工客服，请稍候...";
        }
        return "我还在学习中，您能具体描述一下需求吗？";
    }
}

package com.renewable.ai.controller;

import com.renewable.ai.entity.AiChatMessage;
import com.renewable.ai.entity.AiChatSession;
import com.renewable.ai.service.AiChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/ai")
@CrossOrigin(origins = "*")
public class AiChatController {

    @Autowired
    private AiChatService aiChatService;

    @PostMapping("/sessions")
    public ResponseEntity<AiChatSession> createSession(@RequestBody Map<String, Object> payload) {
        Object userIdObj = payload.get("userId");
        Long userId = null;
        if (userIdObj instanceof Number) {
            userId = ((Number) userIdObj).longValue();
        } else if (userIdObj instanceof String) {
            userId = Long.parseLong((String) userIdObj);
        }
        return ResponseEntity.ok(aiChatService.createSession(userId));
    }

    @GetMapping("/sessions/user/{userId}")
    public ResponseEntity<List<AiChatSession>> getUserSessions(@PathVariable Long userId) {
        return ResponseEntity.ok(aiChatService.getUserSessions(userId));
    }

    @PostMapping("/chat")
    public ResponseEntity<AiChatMessage> sendMessage(@RequestBody Map<String, String> payload) {
        String sessionId = payload.get("sessionId");
        String content = payload.get("content");
        return ResponseEntity.ok(aiChatService.sendMessage(sessionId, content));
    }

    @GetMapping("/history/{sessionId}")
    public ResponseEntity<List<AiChatMessage>> getHistory(@PathVariable String sessionId) {
        return ResponseEntity.ok(aiChatService.getHistory(sessionId));
    }
}

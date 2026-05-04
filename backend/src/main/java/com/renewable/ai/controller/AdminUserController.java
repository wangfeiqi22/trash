package com.renewable.ai.controller;

import com.renewable.ai.entity.User;
import com.renewable.ai.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/users")
@CrossOrigin(origins = "*")
public class AdminUserController {

    @Autowired
    private UserRepository userRepository;

    private User requireAdmin(HttpServletRequest request) {
        Object userIdAttr = request.getAttribute("userId");
        if (!(userIdAttr instanceof Long)) {
            throw new RuntimeException("Unauthorized");
        }
        Long userId = (Long) userIdAttr;
        User me = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("Unauthorized"));
        if (!"admin".equalsIgnoreCase(me.getRole())) {
            throw new RuntimeException("Access denied");
        }
        return me;
    }

    @GetMapping("/pending")
    public ResponseEntity<List<User>> listPendingUsers(HttpServletRequest request) {
        requireAdmin(request);
        List<User> users = userRepository.findByStatus(0);
        // 不展示管理员账号在待审列表里
        users = users.stream().filter(u -> !"admin".equalsIgnoreCase(u.getRole())).toList();
        return ResponseEntity.ok(users);
    }

    @PutMapping("/{id}/approve")
    public ResponseEntity<User> approveUser(@PathVariable Long id, HttpServletRequest request) {
        requireAdmin(request);
        User user = userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
        user.setStatus(1);
        user.setAuditRemark(null);
        user.setUpdatedAt(LocalDateTime.now());
        return ResponseEntity.ok(userRepository.save(user));
    }

    @PutMapping("/{id}/reject")
    public ResponseEntity<User> rejectUser(@PathVariable Long id,
                                           @RequestBody(required = false) Map<String, String> payload,
                                           HttpServletRequest request) {
        requireAdmin(request);
        User user = userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
        user.setStatus(2);
        String remark = payload == null ? null : payload.get("remark");
        user.setAuditRemark(remark == null ? "资料不完整/不符合要求" : remark);
        user.setUpdatedAt(LocalDateTime.now());
        return ResponseEntity.ok(userRepository.save(user));
    }
}


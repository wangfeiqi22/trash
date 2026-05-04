package com.renewable.ai.service;

import com.renewable.ai.entity.User;
import com.renewable.ai.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.renewable.ai.util.SecurityUtil;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public User register(User user) {
        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            throw new RuntimeException("Username already exists");
        }
        user.setPasswordHash(SecurityUtil.hashPassword(user.getPasswordHash()));
        return userRepository.save(user);
    }

    public User login(String username, String password) {
        return userRepository.findByUsername(username)
                .filter(u -> SecurityUtil.verifyPassword(password, u.getPasswordHash()))
                .orElse(null);
    }

    public User findById(Long id) {
        return userRepository.findById(id).orElse(null);
    }
}

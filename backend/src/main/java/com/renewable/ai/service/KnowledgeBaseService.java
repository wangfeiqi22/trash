package com.renewable.ai.service;

import com.renewable.ai.entity.KnowledgeBase;
import com.renewable.ai.repository.KnowledgeBaseRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class KnowledgeBaseService {

    @Autowired
    private KnowledgeBaseRepository knowledgeBaseRepository;

    public List<KnowledgeBase> getAllEntries() {
        return knowledgeBaseRepository.findAll();
    }

    public KnowledgeBase createEntry(KnowledgeBase entry) {
        return knowledgeBaseRepository.save(entry);
    }

    public KnowledgeBase updateEntry(Long id, KnowledgeBase entry) {
        return knowledgeBaseRepository.findById(id).map(existing -> {
            existing.setQuestion(entry.getQuestion());
            existing.setAnswer(entry.getAnswer());
            existing.setCategory(entry.getCategory());
            existing.setIsActive(entry.getIsActive());
            return knowledgeBaseRepository.save(existing);
        }).orElseThrow(() -> new RuntimeException("Entry not found"));
    }

    public void deleteEntry(Long id) {
        knowledgeBaseRepository.deleteById(id);
    }

    public String findAnswer(String query) {
        // Simple keyword matching strategy
        // In production, use Vector DB or Elasticsearch
        List<KnowledgeBase> activeEntries = knowledgeBaseRepository.findByIsActiveTrue();
        
        for (KnowledgeBase entry : activeEntries) {
            if (query.toLowerCase().contains(entry.getQuestion().toLowerCase()) || 
                entry.getQuestion().toLowerCase().contains(query.toLowerCase())) {
                return entry.getAnswer();
            }
        }
        
        // Fallback to keyword search
        List<KnowledgeBase> matches = knowledgeBaseRepository.searchByKeyword(query);
        if (!matches.isEmpty()) {
            return matches.get(0).getAnswer();
        }

        return null; // No match found
    }
}

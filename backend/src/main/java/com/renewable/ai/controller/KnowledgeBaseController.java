package com.renewable.ai.controller;

import com.renewable.ai.entity.KnowledgeBase;
import com.renewable.ai.service.KnowledgeBaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/knowledge-base")
@CrossOrigin(origins = "*")
public class KnowledgeBaseController {

    @Autowired
    private KnowledgeBaseService knowledgeBaseService;

    @GetMapping
    public ResponseEntity<List<KnowledgeBase>> getAllEntries() {
        return ResponseEntity.ok(knowledgeBaseService.getAllEntries());
    }

    @PostMapping
    public ResponseEntity<KnowledgeBase> createEntry(@RequestBody KnowledgeBase entry) {
        return ResponseEntity.ok(knowledgeBaseService.createEntry(entry));
    }

    @PutMapping("/{id}")
    public ResponseEntity<KnowledgeBase> updateEntry(@PathVariable Long id, @RequestBody KnowledgeBase entry) {
        return ResponseEntity.ok(knowledgeBaseService.updateEntry(id, entry));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEntry(@PathVariable Long id) {
        knowledgeBaseService.deleteEntry(id);
        return ResponseEntity.ok().build();
    }
}

package com.renewable.ai.repository;

import com.renewable.ai.entity.KnowledgeBase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface KnowledgeBaseRepository extends JpaRepository<KnowledgeBase, Long> {
    List<KnowledgeBase> findByIsActiveTrue();
    
    // Simple keyword search for MVP
    @Query("SELECT k FROM KnowledgeBase k WHERE k.isActive = true AND (LOWER(k.question) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    List<KnowledgeBase> searchByKeyword(@Param("keyword") String keyword);
}

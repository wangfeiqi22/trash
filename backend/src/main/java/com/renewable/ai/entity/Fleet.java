package com.renewable.ai.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "biz_fleets")
@Data
public class Fleet {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(name = "manager_id")
    private Long managerId;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    private Long userId;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}

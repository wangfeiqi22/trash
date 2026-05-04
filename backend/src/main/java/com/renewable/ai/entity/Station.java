package com.renewable.ai.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.math.BigDecimal;

@Entity
@Table(name = "biz_stations")
@Data
public class Station {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private Integer type; // 1: Clearance (Terminal), 2: Transfer

    @Column(name = "manager_id")
    private Long managerId;

    private String address;
    private BigDecimal lat;
    private BigDecimal lon;

    @Column(name = "region_code")
    private String regionCode;

    // 公告与介绍
    @Column(length = 1000)
    private String announcement; // 清运公告，如营业时间/临时通知等

    @Column(length = 2000)
    private String description; // 站点介绍、服务说明

    // 简要库存概况（文本），用于司机/上游快速了解
    @Column(length = 1000)
    private String inventorySummary;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    private Long userId;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}

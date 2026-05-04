package com.renewable.ai.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "biz_vehicles")
@Data
public class Vehicle {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "fleet_id")
    private Long fleetId;

    @Column(name = "plate_no", unique = true, nullable = false)
    private String plateNo;

    private String type;

    @Column(name = "load_capacity")
    private BigDecimal loadCapacity;

    private Integer status = 1; // 1: Active, 2: Maintenance

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    private Long userId;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}

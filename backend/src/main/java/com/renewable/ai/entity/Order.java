package com.renewable.ai.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.math.BigDecimal;

@Entity
@Table(name = "biz_orders")
@Data
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "order_no", unique = true, nullable = false)
    private String orderNo;

    @Column(name = "creator_id")
    private Long creatorId;

    private Integer type; // 1: Smart, 2: Property, 3: Street, 4: VIP, 5: Individual Self

    private Integer status = 10; // 10: Pending, 20: Assigned, 30: Loading, 40: Transit, 50: Arrived, 60: Done, 90: Cancelled

    @Column(name = "source_type")
    private String sourceType;

    @Column(name = "owner_id")
    private Long ownerId;

    @Column(name = "waste_type")
    private String wasteType;

    @Column(name = "waste_desc")
    private String wasteDesc;

    @Column(name = "est_weight")
    private BigDecimal estWeight;

    @Column(name = "est_volume")
    private BigDecimal estVolume;

    @Column(name = "pickup_address", nullable = false)
    private String pickupAddress;

    @Column(name = "pickup_lat")
    private BigDecimal pickupLat;

    @Column(name = "pickup_lon")
    private BigDecimal pickupLon;

    @Column(name = "station_id")
    private Long stationId;

    @Column(name = "fleet_id")
    private Long fleetId;

    @Column(name = "driver_id")
    private Long driverId;

    @Column(name = "vehicle_id")
    private Long vehicleId;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    private BigDecimal amount;

    private BigDecimal commission;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}

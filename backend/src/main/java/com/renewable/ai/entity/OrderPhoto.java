package com.renewable.ai.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.math.BigDecimal;

@Entity
@Table(name = "biz_order_photos")
@Data
public class OrderPhoto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "order_id")
    private Long orderId;

    @Column(name = "node_type", nullable = false)
    private String nodeType; // create, load, station_entry, unload

    @Column(name = "file_url", nullable = false)
    private String fileUrl;

    @Column(name = "gps_lat")
    private BigDecimal gpsLat;

    @Column(name = "gps_lon")
    private BigDecimal gpsLon;

    @Column(name = "taken_at")
    private LocalDateTime takenAt;

    @Column(name = "is_watermarked")
    private Boolean isWatermarked = false;

    @Column(name = "exif_data", length = 1000)
    private String exifData;

    @PrePersist
    protected void onCreate() {
        if (takenAt == null) {
            takenAt = LocalDateTime.now();
        }
    }
}

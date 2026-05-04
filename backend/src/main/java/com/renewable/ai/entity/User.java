package com.renewable.ai.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "sys_users")
@Data
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    private String passwordHash;

    @Column(unique = true, nullable = false)
    private String phone;

    @Column(nullable = false)
    private String role; // individual, property, street, station, fleet, driver, vip, admin

    private Integer status = 0; // 0: Pending, 1: Active, 2: Rejected, 3: Disabled

    // ====== Registration details (for audit & profile) ======
    // 企业/站点类用户
    private String companyName;
    private String address;
    private String contactName;
    private String contactPhone;

    // 司机类用户
    // driverType: A - 车队司机, B - 个人司机
    private String driverType;
    private String vehiclePlate;
    private String idNumber;
    private String driverLicenseNumber;

    // 资质附件（URL）
    // 企业/站点：营业执照 & 法人身份证
    private String companyLicenseUrl;
    private String companyLegalIdCardUrl;

    // 司机：身份证 & 驾驶证扫描件
    private String driverIdCardUrl;
    private String driverLicenseUrl;

    // 审核备注（驳回原因等）
    private String auditRemark;

    private String avatarUrl;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Fleet ID for drivers (null if freelance)
    @Column(name = "fleet_id")
    private Long fleetId;

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

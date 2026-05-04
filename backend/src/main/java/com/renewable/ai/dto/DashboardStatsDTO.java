package com.renewable.ai.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DashboardStatsDTO {
    private Long totalOrders;
    private Long completedOrders;
    private Long pendingOrders;
    private Double completionRate;
}

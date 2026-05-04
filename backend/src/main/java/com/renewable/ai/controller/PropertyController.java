package com.renewable.ai.controller;

import com.renewable.ai.entity.Order;
import com.renewable.ai.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/property")
@CrossOrigin(origins = "*")
public class PropertyController {

    @Autowired
    private OrderRepository orderRepository;

    @GetMapping("/dashboard/stats")
    public ResponseEntity<?> getDashboardStats(@RequestParam Long userId) {
        // In a real B2B scenario, a Property Manager might manage multiple users or a "Community ID".
        // For this MVP, we assume the Property Manager IS the user creating bulk orders.
        
        List<Order> orders = orderRepository.findByCreatorId(userId);
        
        int totalOrders = orders.size();
        long pendingOrders = orders.stream().filter(o -> o.getStatus() < 60).count();
        BigDecimal totalWeight = orders.stream()
                .map(Order::getEstWeight)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        BigDecimal totalCost = orders.stream()
                .map(o -> o.getAmount() != null ? o.getAmount() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        BigDecimal totalCommission = orders.stream()
                .map(o -> o.getCommission() != null ? o.getCommission() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        // If totalCost is zero (no real data yet), fallback to mock for demo
        if (totalCost.compareTo(BigDecimal.ZERO) == 0 && totalWeight.compareTo(BigDecimal.ZERO) > 0) {
             totalCost = totalWeight.multiply(new BigDecimal("50"));
             totalCommission = totalCost.multiply(new BigDecimal("0.05")); // 5% Commission
        }

        Map<String, Object> stats = new HashMap<>();
        stats.put("totalOrders", totalOrders);
        stats.put("pendingOrders", pendingOrders);
        stats.put("totalWeight", totalWeight);
        stats.put("totalCost", totalCost);
        stats.put("totalCommission", totalCommission);
        
        return ResponseEntity.ok(stats);
    }
}

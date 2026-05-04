package com.renewable.ai.controller;

import com.renewable.ai.entity.Order;
import com.renewable.ai.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/station/ops")
@CrossOrigin(origins = "*")
public class StationOperationController {

    @Autowired
    private OrderRepository orderRepository;

    // Get orders arriving at this station (Status 40: Transporting -> 50: Arrived)
    @GetMapping("/incoming")
    public ResponseEntity<?> getIncomingOrders() {
        // In real app, filter by stationId associated with current user
        List<Order> orders = orderRepository.findAll().stream()
                .filter(o -> o.getStatus() == 40 || o.getStatus() == 50)
                .collect(Collectors.toList());
        return ResponseEntity.ok(orders);
    }

    // Confirm Arrival (Driver arrived at station)
    @PutMapping("/confirm-arrival/{orderId}")
    public ResponseEntity<?> confirmArrival(@PathVariable Long orderId) {
        Order order = orderRepository.findById(orderId).orElse(null);
        if (order == null) return ResponseEntity.notFound().build();

        if (order.getStatus() == 40) {
            order.setStatus(50); // Arrived
            order.setUpdatedAt(LocalDateTime.now());
            orderRepository.save(order);
            return ResponseEntity.ok("Arrival confirmed");
        }
        return ResponseEntity.badRequest().body("Order not in transporting state");
    }

    // Process Weigh-in and Complete (Status 50 -> 60)
    @PutMapping("/weigh-in/{orderId}")
    public ResponseEntity<?> weighInAndComplete(@PathVariable Long orderId, @RequestParam Double weight) {
        Order order = orderRepository.findById(orderId).orElse(null);
        if (order == null) return ResponseEntity.notFound().build();

        if (order.getStatus() == 50) {
            // In real app, we might update actual weight here
            // order.setActualWeight(BigDecimal.valueOf(weight));
            
            order.setStatus(60); // Completed
            order.setUpdatedAt(LocalDateTime.now());
            orderRepository.save(order);
            return ResponseEntity.ok("Order completed");
        }
        return ResponseEntity.badRequest().body("Order not arrived yet");
    }

    @GetMapping("/history")
    public ResponseEntity<?> getStationHistory() {
        List<Order> orders = orderRepository.findAll().stream()
                .filter(o -> o.getStatus() == 60)
                .collect(Collectors.toList());
        return ResponseEntity.ok(orders);
    }
}

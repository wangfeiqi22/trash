package com.renewable.ai.controller;

import com.renewable.ai.entity.Order;
import com.renewable.ai.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.List;

@RestController
@RequestMapping("/api/orders")
@CrossOrigin(origins = "*")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @PostMapping
    public ResponseEntity<Order> createOrder(@RequestBody Order order) {
        return ResponseEntity.ok(orderService.createOrder(order));
    }
    
    @GetMapping("/{orderId}")
    public ResponseEntity<Order> getOrderById(@PathVariable Long orderId) {
        Order order = orderService.getOrderById(orderId);
        if (order != null) {
            return ResponseEntity.ok(order);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/pool")
    public ResponseEntity<List<Order>> getPoolOrders() {
        return ResponseEntity.ok(orderService.getPoolOrders());
    }
    
    @PutMapping("/{orderId}/grab")
    public ResponseEntity<Order> grabOrder(@PathVariable Long orderId, @RequestParam Long driverId) {
        return ResponseEntity.ok(orderService.grabOrder(orderId, driverId));
    }

    @PutMapping("/{orderId}/accept")
    public ResponseEntity<Order> acceptOrder(@PathVariable Long orderId, @RequestParam Long driverId) {
        // Only allow if order is in Assigned (20) state and assigned to this driver
        // Or if it's a pool order (10) -> handled by grab (which we removed for freelance)
        // Here we assume "Accept" means confirming a dispatched order (Status 20 -> 25 Accepted/OnWay)
        // Or we can jump directly to 30 (Loading) if 'Start Loading' implies acceptance.
        // Let's assume user wants an explicit 'Accept' step before 'Start Loading'.
        
        // However, current flow is 20 (Assigned) -> 30 (Loading).
        // Let's add a status 25 'Accepted' or just use 'Start Loading' as acceptance.
        // If user specifically asked for "Accept" function, maybe they mean for Fleet Drivers to acknowledge?
        
        // Let's implement a status update to 25 "Driver Accepted"
        return ResponseEntity.ok(orderService.updateStatus(orderId, 25));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Order>> getUserOrders(@PathVariable Long userId) {
        return ResponseEntity.ok(orderService.getOrdersByUser(userId));
    }

    @PutMapping("/{orderId}/status")
    public ResponseEntity<Order> updateStatus(@PathVariable Long orderId, @RequestParam Integer status) {
        return ResponseEntity.ok(orderService.updateStatus(orderId, status));
    }

    @PutMapping("/{orderId}/assign")
    public ResponseEntity<Order> assignOrder(@PathVariable Long orderId, @RequestBody Map<String, Object> payload) {
        Long fleetId = payload.get("fleetId") instanceof Number ? ((Number) payload.get("fleetId")).longValue() : null;
        Long driverId = payload.get("driverId") instanceof Number ? ((Number) payload.get("driverId")).longValue() : null;
        Long vehicleId = payload.get("vehicleId") instanceof Number ? ((Number) payload.get("vehicleId")).longValue() : null;
        return ResponseEntity.ok(orderService.assignOrder(orderId, fleetId, driverId, vehicleId));
    }

    @PostMapping("/self-create")
    public ResponseEntity<?> createSelfOrder(@RequestBody Map<String, Object> payload) {
        try {
            // Extract fields manually since payload structure differs slightly from Order entity
            Order order = new Order();
            order.setPickupAddress((String) payload.get("pickupAddress"));
            order.setWasteType((String) payload.get("wasteType"));
            // Handle numeric conversion safely
            Object weightObj = payload.get("estWeight");
            if (weightObj instanceof Integer) {
                order.setEstWeight(java.math.BigDecimal.valueOf((Integer) weightObj));
            } else if (weightObj instanceof Double) {
                order.setEstWeight(java.math.BigDecimal.valueOf((Double) weightObj));
            }
            
            // Shipper info (stored in wasteDesc or separate fields if entity updated)
            String shipperInfo = "托运人: " + payload.get("shipperName") + " (" + payload.get("shipperPhone") + ")";
            order.setWasteDesc(shipperInfo);
            
            Long driverId = ((Number) payload.get("driverId")).longValue();
            
            Order created = orderService.createSelfOrder(order, driverId);
            return ResponseEntity.ok(created);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    @GetMapping("/{orderId}/logs")
    public ResponseEntity<List<com.renewable.ai.entity.OrderLog>> getOrderLogs(@PathVariable Long orderId) {
        return ResponseEntity.ok(orderService.getOrderLogs(orderId));
    }

    @Autowired
    private com.renewable.ai.repository.OrderPhotoRepository orderPhotoRepository;

    @GetMapping("/{orderId}/photos")
    public ResponseEntity<List<com.renewable.ai.entity.OrderPhoto>> getOrderPhotos(@PathVariable Long orderId) {
        return ResponseEntity.ok(orderPhotoRepository.findByOrderId(orderId));
    }

    @GetMapping("/estimate-trucks")
    public ResponseEntity<Map<String, Object>> estimateTrucks(@RequestParam("volume") Double volume) {
        if (volume == null || volume < 0) {
            return ResponseEntity.badRequest().body(Map.of("error", "无效的垃圾方数"));
        }
        
        double capacity = 6.0;
        int requiredTrucks = (int) Math.ceil(volume / capacity);
        int fullTrucks = (int) Math.floor(volume / capacity);
        double remainingVolume = volume % capacity;
        
        // Handle floating point precision
        remainingVolume = Math.round(remainingVolume * 10.0) / 10.0;
        if (remainingVolume == capacity) {
            fullTrucks++;
            remainingVolume = 0;
        }

        String suggestion;
        if (fullTrucks == 0 && remainingVolume == 0) {
            suggestion = "无需车辆";
        } else if (fullTrucks == 0) {
            // Remove .0 if it's an integer
            String remStr = (remainingVolume == (long) remainingVolume) ? String.format("%d", (long) remainingVolume) : String.valueOf(remainingVolume);
            suggestion = "1车" + remStr + "方";
        } else if (remainingVolume > 0) {
            String remStr = (remainingVolume == (long) remainingVolume) ? String.format("%d", (long) remainingVolume) : String.valueOf(remainingVolume);
            suggestion = fullTrucks + "车满载(6方) + 1车" + remStr + "方";
        } else {
            suggestion = fullTrucks + "车满载(6方)";
        }
        
        Map<String, Object> result = Map.of(
            "volume", volume,
            "truckCapacity", capacity,
            "requiredTrucks", requiredTrucks,
            "fullTrucks", fullTrucks,
            "remainingVolume", remainingVolume,
            "suggestion", suggestion
        );
        return ResponseEntity.ok(result);
    }
}

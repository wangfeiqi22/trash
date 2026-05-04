package com.renewable.ai.service;

import com.renewable.ai.dto.DashboardStatsDTO;
import com.renewable.ai.entity.Order;
import com.renewable.ai.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private StationService stationService;

    @Autowired
    private com.renewable.ai.repository.OrderLogRepository orderLogRepository;

    private void logStatusChange(Order order, Integer oldStatus, Integer newStatus, String action, Long operatorId, String remark) {
        com.renewable.ai.entity.OrderLog log = new com.renewable.ai.entity.OrderLog();
        log.setOrderId(order.getId());
        log.setOldStatus(oldStatus);
        log.setNewStatus(newStatus);
        log.setActionName(action);
        log.setOperatorId(operatorId);
        log.setRemark(remark);
        
        if (operatorId != null) {
            userRepository.findById(operatorId).ifPresent(u -> log.setOperatorName(u.getUsername()));
        } else {
            log.setOperatorName("System");
        }
        
        orderLogRepository.save(log);
    }

    private static final java.util.concurrent.atomic.AtomicInteger sequence = new java.util.concurrent.atomic.AtomicInteger(1);
    private static String currentDateStr = java.time.LocalDate.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd"));

    private synchronized String generateOrderNo() {
        String todayStr = java.time.LocalDate.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd"));
        if (!todayStr.equals(currentDateStr)) {
            currentDateStr = todayStr;
            sequence.set(1);
        }
        int seq = sequence.getAndIncrement();
        return String.format("QY%s%05d", todayStr, seq);
    }

    public List<com.renewable.ai.entity.OrderLog> getOrderLogs(Long orderId) {
        return orderLogRepository.findByOrderIdOrderByCreatedAtDesc(orderId);
    }

    public Order createOrder(Order order) {
        order.setOrderNo(generateOrderNo());
        if (order.getStatus() == null) {
            order.setStatus(10); // Default to Pool
        }
        
        // Auto-Dispatch Logic: Find nearest station if not set
        if (order.getStationId() == null && order.getPickupLat() != null && order.getPickupLon() != null) {
            // Type 1: Clearance Station (Terminal)
            com.renewable.ai.entity.Station nearest = stationService.findNearestStation(
                order.getPickupLat().doubleValue(), 
                order.getPickupLon().doubleValue(), 
                1
            );
            if (nearest != null) {
                order.setStationId(nearest.getId());
            }
        }
        
        return orderRepository.save(order);
    }

    public List<Order> getOrdersByUser(Long userId) {
        return orderRepository.findByCreatorIdOrDriverId(userId, userId);
    }
    
    public Order getOrderById(Long orderId) {
        return orderRepository.findById(orderId).orElse(null);
    }
    
    public List<Order> getPoolOrders() {
        return orderRepository.findByStatus(10);
    }
    
    @Autowired
    private com.renewable.ai.repository.UserRepository userRepository;

    public Order grabOrder(Long orderId, Long driverId) {
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new RuntimeException("Order not found"));
        if (order.getStatus() != 10) {
            throw new RuntimeException("Order not available");
        }
        
        // Strict Validation: Only Type A (Fleet Drivers) can grab
        com.renewable.ai.entity.User driver = userRepository.findById(driverId).orElseThrow();
        if (driver.getFleetId() == null) {
            throw new RuntimeException("Access Denied: Freelance drivers cannot grab pool orders.");
        }

        order.setStatus(25); // Accepted (driver grabbed it directly)
        order.setDriverId(driverId);
        order.setFleetId(driver.getFleetId()); // Assign to fleet as well
        Order savedOrder = orderRepository.save(order);
        logStatusChange(savedOrder, 10, 25, "Grab", driverId, "司机抢单成功");
        return savedOrder;
    }

    public Order updateStatus(Long orderId, Integer status) {
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new RuntimeException("Order not found"));
        Integer oldStatus = order.getStatus();
        order.setStatus(status);
        Order savedOrder = orderRepository.save(order);
        
        // Infer action name based on status
        String action = "Update Status";
        String remark = "状态更新: " + oldStatus + " -> " + status;
        if (status == 25) { action = "Accept"; remark = "司机接单"; }
        else if (status == 30) { action = "Load"; remark = "开始装车"; }
        else if (status == 40) { action = "Depart"; remark = "开始运输"; }
        else if (status == 50) { action = "Arrive"; remark = "到达站点"; }
        else if (status == 60) { action = "Complete"; remark = "订单完成"; }
        
        // For simplicity, we assume the current driver is the operator if available, or system
        Long operatorId = order.getDriverId();
        
        logStatusChange(savedOrder, oldStatus, status, action, operatorId, remark);
        return savedOrder;
    }

    public Order assignOrder(Long orderId, Long fleetId, Long driverId, Long vehicleId) {
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new RuntimeException("Order not found"));
        Integer oldStatus = order.getStatus();
        order.setFleetId(fleetId);
        order.setDriverId(driverId);
        order.setVehicleId(vehicleId);
        order.setStatus(20); // Assigned
        Order savedOrder = orderRepository.save(order);
        logStatusChange(savedOrder, oldStatus, 20, "Assign", null, "车队指派订单");
        return savedOrder;
    }

    public Order createSelfOrder(Order order, Long driverId) {
        // Idempotency check: prevent duplicate submission within 1 minute
        java.time.LocalDateTime oneMinuteAgo = java.time.LocalDateTime.now().minusMinutes(1);
        if (orderRepository.existsByDriverIdAndPickupAddressAndCreatedAtAfter(driverId, order.getPickupAddress(), oneMinuteAgo)) {
            throw new RuntimeException("重复提交：您刚刚已在该地址创建了相同清运单");
        }

        order.setOrderNo(generateOrderNo());
        order.setDriverId(driverId);
        order.setCreatorId(driverId);
        order.setOwnerId(driverId); // Assign owner
        order.setSourceType("SELF_CREATE"); // Identify self-created scenario
        order.setStatus(20); // Directly assigned to self (Ready to pickup)

        Order savedOrder = orderRepository.save(order);
        logStatusChange(savedOrder, null, 20, "Self Create", driverId, "个人司机自主建单并自动归属");
        return savedOrder;
    }

    public org.springframework.data.domain.Page<Order> getDriverHistory(Long driverId, int page, int size, java.time.LocalDateTime startDate, java.time.LocalDateTime endDate) {
        org.springframework.data.domain.Pageable pageable =
                org.springframework.data.domain.PageRequest.of(page, size,
                        org.springframework.data.domain.Sort.by("createdAt").descending());

        // 历史任务：仅展示该司机已完成的订单
        java.util.List<Integer> statuses = java.util.Collections.singletonList(60); // 60: Done

        return orderRepository.findHistoryTasks(driverId, statuses, startDate, endDate, pageable);
    }

    public DashboardStatsDTO getDashboardStats() {
        long total = orderRepository.count();
        long completed = orderRepository.countByStatus(60);
        long pending = orderRepository.countByStatus(10);
        double rate = total == 0 ? 0 : (double) completed / total * 100;
        return new DashboardStatsDTO(total, completed, pending, rate);
    }
}

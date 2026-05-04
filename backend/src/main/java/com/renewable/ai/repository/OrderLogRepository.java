package com.renewable.ai.repository;

import com.renewable.ai.entity.OrderLog;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface OrderLogRepository extends JpaRepository<OrderLog, Long> {
    List<OrderLog> findByOrderIdOrderByCreatedAtDesc(Long orderId);
}

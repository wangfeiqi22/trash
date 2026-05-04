package com.renewable.ai.repository;

import com.renewable.ai.entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByCreatorId(Long creatorId);
    List<Order> findByCreatorIdOrDriverId(Long creatorId, Long driverId);
    List<Order> findByStatus(Integer status);
    long countByStatus(Integer status);

    boolean existsByDriverIdAndPickupAddressAndCreatedAtAfter(Long driverId, String pickupAddress, LocalDateTime createdAt);

    @Query("SELECT o FROM Order o WHERE (o.driverId = :driverId OR o.creatorId = :driverId) AND o.status IN :statuses AND o.updatedAt >= :startDate AND o.updatedAt <= :endDate")
    Page<Order> findHistoryTasks(
        @Param("driverId") Long driverId,
        @Param("statuses") List<Integer> statuses,
        @Param("startDate") LocalDateTime startDate,
        @Param("endDate") LocalDateTime endDate,
        Pageable pageable
    );
}

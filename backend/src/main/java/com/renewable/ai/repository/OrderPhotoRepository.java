package com.renewable.ai.repository;

import com.renewable.ai.entity.OrderPhoto;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface OrderPhotoRepository extends JpaRepository<OrderPhoto, Long> {
    List<OrderPhoto> findByOrderId(Long orderId);
}

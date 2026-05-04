package com.renewable.ai.repository;

import com.renewable.ai.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByStationId(Long stationId);
    List<Product> findByStatus(Integer status);
}

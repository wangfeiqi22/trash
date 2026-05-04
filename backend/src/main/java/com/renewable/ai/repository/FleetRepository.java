package com.renewable.ai.repository;

import com.renewable.ai.entity.Fleet;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FleetRepository extends JpaRepository<Fleet, Long> {
}

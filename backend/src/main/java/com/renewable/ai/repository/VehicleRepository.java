package com.renewable.ai.repository;

import com.renewable.ai.entity.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface VehicleRepository extends JpaRepository<Vehicle, Long> {
    List<Vehicle> findByFleetId(Long fleetId);
}

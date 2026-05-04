package com.renewable.ai.service;

import com.renewable.ai.entity.Fleet;
import com.renewable.ai.entity.Vehicle;
import com.renewable.ai.repository.FleetRepository;
import com.renewable.ai.repository.VehicleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FleetService {

    @Autowired
    private FleetRepository fleetRepository;

    @Autowired
    private VehicleRepository vehicleRepository;

    public Fleet createFleet(Fleet fleet) {
        return fleetRepository.save(fleet);
    }

    public List<Fleet> getAllFleets() {
        return fleetRepository.findAll();
    }

    public Vehicle addVehicle(Vehicle vehicle) {
        return vehicleRepository.save(vehicle);
    }

    public List<Vehicle> getVehiclesByFleet(Long fleetId) {
        return vehicleRepository.findByFleetId(fleetId);
    }
}

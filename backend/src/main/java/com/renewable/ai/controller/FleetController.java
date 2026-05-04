package com.renewable.ai.controller;

import com.renewable.ai.entity.Fleet;
import com.renewable.ai.entity.Vehicle;
import com.renewable.ai.service.FleetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/fleets")
@CrossOrigin(origins = "*")
public class FleetController {

    @Autowired
    private FleetService fleetService;

    @PostMapping
    public ResponseEntity<Fleet> createFleet(@RequestBody Fleet fleet) {
        return ResponseEntity.ok(fleetService.createFleet(fleet));
    }

    @GetMapping
    public ResponseEntity<List<Fleet>> getAllFleets() {
        return ResponseEntity.ok(fleetService.getAllFleets());
    }

    @PostMapping("/vehicles")
    public ResponseEntity<Vehicle> addVehicle(@RequestBody Vehicle vehicle) {
        return ResponseEntity.ok(fleetService.addVehicle(vehicle));
    }

    @GetMapping("/{fleetId}/vehicles")
    public ResponseEntity<List<Vehicle>> getVehiclesByFleet(@PathVariable Long fleetId) {
        return ResponseEntity.ok(fleetService.getVehiclesByFleet(fleetId));
    }
}

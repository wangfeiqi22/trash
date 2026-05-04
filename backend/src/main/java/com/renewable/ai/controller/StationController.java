package com.renewable.ai.controller;

import com.renewable.ai.entity.Station;
import com.renewable.ai.service.StationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/stations")
@CrossOrigin(origins = "*")
public class StationController {

    @Autowired
    private StationService stationService;

    @PostMapping
    public ResponseEntity<Station> createStation(@RequestBody Station station) {
        return ResponseEntity.ok(stationService.createStation(station));
    }

    @GetMapping
    public ResponseEntity<List<Station>> getAllStations() {
        return ResponseEntity.ok(stationService.getAllStations());
    }

    @GetMapping("/type/{type}")
    public ResponseEntity<List<Station>> getStationsByType(@PathVariable Integer type) {
        return ResponseEntity.ok(stationService.getStationsByType(type));
    }
}

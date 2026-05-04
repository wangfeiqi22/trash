package com.renewable.ai.service;

import com.renewable.ai.entity.Station;
import com.renewable.ai.repository.StationRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
public class StationService {

    private static final int NEAREST_SEARCH_BATCH_SIZE = 100;

    @Autowired
    private StationRepository stationRepository;

    public Station createStation(Station station) {
        return stationRepository.save(station);
    }

    public List<Station> getAllStations() {
        return stationRepository.findAll();
    }

    public List<Station> getStationsByType(Integer type) {
        return stationRepository.findByType(type);
    }

    public Optional<Station> getStationById(Long id) {
        return stationRepository.findById(id);
    }

    public Optional<Station> getStationByManagerId(Long managerId) {
        List<Station> list = stationRepository.findByManagerId(managerId);
        if (list == null || list.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(list.get(0));
    }

    public Station updateStationProfile(Long id, Map<String, String> payload) {
        Station station = stationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Station not found"));

        if (payload.containsKey("announcement")) {
            station.setAnnouncement(payload.get("announcement"));
        }
        if (payload.containsKey("description")) {
            station.setDescription(payload.get("description"));
        }
        if (payload.containsKey("inventorySummary")) {
            station.setInventorySummary(payload.get("inventorySummary"));
        }

        return stationRepository.save(station);
    }

    public Station findNearestStation(double lat, double lon, Integer type) {
        Station nearest = null;
        double minDistance = Double.MAX_VALUE;
        int pageNumber = 0;

        Page<Station> page;
        do {
            page = stationRepository.findCandidatesForNearestSearch(type, PageRequest.of(pageNumber, NEAREST_SEARCH_BATCH_SIZE));
            for (Station s : page.getContent()) {
                double dist = Math.sqrt(
                        Math.pow(s.getLat().doubleValue() - lat, 2)
                                + Math.pow(s.getLon().doubleValue() - lon, 2));
                if (dist < minDistance) {
                    minDistance = dist;
                    nearest = s;
                }
            }
            pageNumber++;
        } while (page.hasNext());

        return nearest;
    }
}

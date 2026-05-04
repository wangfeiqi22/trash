package com.renewable.ai.service;

import com.renewable.ai.entity.Order;
import com.renewable.ai.entity.User;
import com.renewable.ai.entity.Vehicle;
import com.renewable.ai.entity.Fleet;
import com.renewable.ai.entity.Station;
import com.renewable.ai.repository.OrderRepository;
import com.renewable.ai.repository.UserRepository;
import com.renewable.ai.repository.VehicleRepository;
import com.renewable.ai.repository.FleetRepository;
import com.renewable.ai.repository.StationRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class ResourceOwnershipService {

    @Autowired private OrderRepository orderRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private VehicleRepository vehicleRepository;
    @Autowired private FleetRepository fleetRepository;
    @Autowired private StationRepository stationRepository;

    public boolean isAdmin(Long userId) {
        User user = userRepository.findById(userId).orElse(null);
        return user != null && "admin".equalsIgnoreCase(user.getRole());
    }

    public boolean canAccessOrder(Long userId, Long orderId) {
        if (isAdmin(userId)) return true;
        Order order = orderRepository.findById(orderId).orElse(null);
        if (order == null) return false;
        if (order.getOwnerId() != null && order.getOwnerId().equals(userId)) return true;
        if (order.getCreatorId() != null && order.getCreatorId().equals(userId)) return true;
        if (order.getDriverId() != null && order.getDriverId().equals(userId)) return true;
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) return false;
        String role = user.getRole();
        if ("station".equals(role) && order.getStationId() != null) {
            Station station = stationRepository.findById(order.getStationId()).orElse(null);
            return station != null && station.getUserId() != null && station.getUserId().equals(userId);
        }
        if ("fleet".equals(role) && order.getVehicleId() != null) {
            Vehicle vehicle = vehicleRepository.findById(order.getVehicleId()).orElse(null);
            return vehicle != null && vehicle.getFleetId() != null && isUserFleetOwner(userId, vehicle.getFleetId());
        }
        return false;
    }

    public boolean canModifyOrder(Long userId, Long orderId) {
        return canAccessOrder(userId, orderId);
    }

    public boolean canAccessVehicle(Long userId, Long vehicleId) {
        if (isAdmin(userId)) return true;
        Vehicle vehicle = vehicleRepository.findById(vehicleId).orElse(null);
        if (vehicle == null) return false;
        if (vehicle.getUserId() != null && vehicle.getUserId().equals(userId)) return true;
        if (vehicle.getFleetId() != null && isUserFleetOwner(userId, vehicle.getFleetId())) return true;
        return false;
    }

    public boolean canModifyVehicle(Long userId, Long vehicleId) {
        return canAccessVehicle(userId, vehicleId);
    }

    public boolean canAccessStation(Long userId, Long stationId) {
        if (isAdmin(userId)) return true;
        Station station = stationRepository.findById(stationId).orElse(null);
        if (station == null) return false;
        if (station.getUserId() != null && station.getUserId().equals(userId)) return true;
        return false;
    }

    public boolean canAccessFleet(Long userId, Long fleetId) {
        if (isAdmin(userId)) return true;
        return isUserFleetOwner(userId, fleetId);
    }

    private boolean isUserFleetOwner(Long userId, Long fleetId) {
        Fleet fleet = fleetRepository.findById(fleetId).orElse(null);
        return fleet != null && fleet.getUserId() != null && fleet.getUserId().equals(userId);
    }

    public void checkOrderAccess(Long userId, Long orderId) {
        if (!canAccessOrder(userId, orderId)) {
            throw new SecurityException("无权访问该订单");
        }
    }

    public void checkOrderModification(Long userId, Long orderId) {
        if (!canModifyOrder(userId, orderId)) {
            throw new SecurityException("无权修改该订单");
        }
    }
}

package com.renewable.ai.config;

import com.renewable.ai.entity.*;
import com.renewable.ai.repository.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;

@Slf4j
@Configuration
public class DataSeeder {

    @Bean
    CommandLineRunner initDatabase(UserRepository userRepository,
                                   StationRepository stationRepository,
                                   FleetRepository fleetRepository,
                                   VehicleRepository vehicleRepository,
                                   KnowledgeBaseRepository kbRepository,
                                   OrderRepository orderRepository) {
        return args -> {
            // ============================================================
            // 账号初始化已禁用
            // 原因：默认账号存在安全风险，账号创建已改为运维手册管理
            // 参见：docs/ops/initialization-guide.md
            // 禁止在代码中硬编码任何账号信息
            // ============================================================

            if (stationRepository.count() == 0) {
                Station s1 = new Station();
                s1.setName("朝阳区第一清运站");
                s1.setType(1);
                s1.setAddress("北京市朝阳区xx路88号");
                s1.setLat(new BigDecimal("39.9042"));
                s1.setLon(new BigDecimal("116.4074"));
                stationRepository.save(s1);

                Station s2 = new Station();
                s2.setName("海淀中转站");
                s2.setType(2);
                s2.setAddress("北京市海淀区xx街12号");
                stationRepository.save(s2);
                log.info("DataSeeder: Stations initialized");
            }

            if (fleetRepository.count() == 0) {
                Fleet f1 = new Fleet();
                f1.setName("第一车队");
                f1 = fleetRepository.save(f1);

                Vehicle v1 = new Vehicle();
                v1.setFleetId(f1.getId());
                v1.setPlateNo("京A88888");
                v1.setType("压缩车");
                v1.setLoadCapacity(new BigDecimal("5.0"));
                vehicleRepository.save(v1);
                log.info("DataSeeder: Fleet and Vehicle initialized");
            }

            if (kbRepository.count() == 0) {
                KnowledgeBase k1 = new KnowledgeBase();
                k1.setQuestion("清运费用是多少？");
                k1.setAnswer("生活垃圾 50元/吨，建筑垃圾 80元/吨，大件垃圾按件计费。");
                k1.setCategory("pricing");
                kbRepository.save(k1);

                KnowledgeBase k2 = new KnowledgeBase();
                k2.setQuestion("工作时间是几点？");
                k2.setAnswer("我们的工作时间是每天 8:00 - 20:00，节假日不休息。");
                k2.setCategory("guide");
                kbRepository.save(k2);
                log.info("DataSeeder: Knowledge Base initialized");
            }

            if (stationRepository.count() > 0 && fleetRepository.count() > 0) {
                if (orderRepository.findByStatus(10).isEmpty()) {
                    Order o1 = new Order();
                    o1.setOrderNo("QY" + System.currentTimeMillis());
                    o1.setPickupAddress("北京市朝阳区高碑店路22号");
                    o1.setWasteType("CONSTRUCTION");
                    o1.setEstWeight(new BigDecimal("1200.5"));
                    o1.setAmount(new BigDecimal("600.0"));
                    o1.setStatus(10);
                    o1.setStationId(stationRepository.findAll().get(0).getId());
                    orderRepository.save(o1);

                    Order o2 = new Order();
                    o2.setOrderNo("QY" + (System.currentTimeMillis() + 1));
                    o2.setPickupAddress("北京市海淀区中关村软件园");
                    o2.setWasteType("BULKY");
                    o2.setEstWeight(new BigDecimal("500.0"));
                    o2.setAmount(new BigDecimal("250.0"));
                    o2.setStatus(10);
                    o2.setStationId(stationRepository.findAll().get(1).getId());
                    orderRepository.save(o2);
                    log.info("DataSeeder: Mock orders in pool initialized");
                }
            }
        };
    }
}

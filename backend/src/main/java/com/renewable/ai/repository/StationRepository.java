package com.renewable.ai.repository;

import com.renewable.ai.entity.Station;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface StationRepository extends JpaRepository<Station, Long> {
    List<Station> findByType(Integer type);
    List<Station> findByManagerId(Long managerId);
    List<Station> findByUserId(Long userId);
    @Query("SELECT s FROM Station s WHERE (:type IS NULL OR s.type = :type)")
    Page<Station> findCandidatesForNearestSearch(@Param("type") Integer type, Pageable pageable);
}

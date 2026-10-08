package com.animalmonitoring.repository;

import com.animalmonitoring.entity.Detection;
import com.animalmonitoring.entity.RiskLevel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface DetectionRepository extends JpaRepository<Detection, Long> {

    List<Detection> findByIntrusionDetected(boolean intrusionDetected);

    List<Detection> findByRiskLevel(RiskLevel riskLevel);

    List<Detection> findByAnimalId(Long animalId);

    List<Detection> findByZoneId(Long zoneId);

    List<Detection> findByTimestampBetween(LocalDateTime from, LocalDateTime to);

    List<Detection> findByRiskLevelIn(List<RiskLevel> riskLevels);

    long countByIntrusionDetectedTrue();

    long countByRiskLevel(RiskLevel riskLevel);
}

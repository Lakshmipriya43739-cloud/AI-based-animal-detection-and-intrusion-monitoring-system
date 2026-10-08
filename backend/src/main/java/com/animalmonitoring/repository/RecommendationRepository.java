package com.animalmonitoring.repository;

import com.animalmonitoring.entity.Recommendation;
import com.animalmonitoring.entity.RiskLevel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RecommendationRepository extends JpaRepository<Recommendation, Long> {

    List<Recommendation> findByActive(boolean active);

    List<Recommendation> findByAnimalId(Long animalId);

    List<Recommendation> findByRiskLevel(RiskLevel riskLevel);

    List<Recommendation> findByAnimalIdAndRiskLevelAndActive(Long animalId, RiskLevel riskLevel, boolean active);
}

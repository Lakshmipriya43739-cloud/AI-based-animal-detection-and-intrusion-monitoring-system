package com.animalmonitoring.repository;

import com.animalmonitoring.entity.Animal;
import com.animalmonitoring.entity.RiskLevel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AnimalRepository extends JpaRepository<Animal, Long> {

    List<Animal> findByActive(boolean active);

    List<Animal> findByDefaultRiskLevel(RiskLevel riskLevel);

    boolean existsByNameIgnoreCase(String name);
}

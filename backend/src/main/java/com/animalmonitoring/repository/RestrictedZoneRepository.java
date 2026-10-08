package com.animalmonitoring.repository;

import com.animalmonitoring.entity.RestrictedZone;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RestrictedZoneRepository extends JpaRepository<RestrictedZone, Long> {

    List<RestrictedZone> findByActive(boolean active);

    boolean existsByNameIgnoreCase(String name);
}

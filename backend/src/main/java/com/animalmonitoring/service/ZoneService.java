package com.animalmonitoring.service;

import com.animalmonitoring.dto.request.ZoneRequest;
import com.animalmonitoring.dto.response.ZoneResponse;
import com.animalmonitoring.entity.RestrictedZone;
import com.animalmonitoring.exception.DuplicateResourceException;
import com.animalmonitoring.exception.ResourceNotFoundException;
import com.animalmonitoring.repository.RestrictedZoneRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ZoneService {

    private static final Logger log = LoggerFactory.getLogger(ZoneService.class);

    private final RestrictedZoneRepository zoneRepository;

    public ZoneService(RestrictedZoneRepository zoneRepository) {
        this.zoneRepository = zoneRepository;
    }

    @Transactional(readOnly = true)
    public List<ZoneResponse> getAllZones() {
        return zoneRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ZoneResponse> getActiveZones() {
        return zoneRepository.findByActive(true).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ZoneResponse getZoneById(Long id) {
        return mapToResponse(findById(id));
    }

    @Transactional
    public ZoneResponse createZone(ZoneRequest request) {
        if (zoneRepository.existsByNameIgnoreCase(request.getName())) {
            throw new DuplicateResourceException("Zone already exists with name: " + request.getName());
        }
        RestrictedZone zone = RestrictedZone.builder()
                .name(request.getName())
                .description(request.getDescription())
                .location(request.getLocation())
                .boundaryData(request.getBoundaryData())
                .active(request.isActive())
                .build();
        RestrictedZone saved = zoneRepository.save(zone);
        log.info("Created restricted zone '{}' with id {}", saved.getName(), saved.getId());
        return mapToResponse(saved);
    }

    @Transactional
    public ZoneResponse updateZone(Long id, ZoneRequest request) {
        RestrictedZone zone = findById(id);

        if (!zone.getName().equalsIgnoreCase(request.getName())
                && zoneRepository.existsByNameIgnoreCase(request.getName())) {
            throw new DuplicateResourceException("Zone already exists with name: " + request.getName());
        }

        zone.setName(request.getName());
        zone.setDescription(request.getDescription());
        zone.setLocation(request.getLocation());
        zone.setBoundaryData(request.getBoundaryData());
        zone.setActive(request.isActive());

        RestrictedZone saved = zoneRepository.save(zone);
        log.info("Updated restricted zone id {}", saved.getId());
        return mapToResponse(saved);
    }

    @Transactional
    public void deleteZone(Long id) {
        RestrictedZone zone = findById(id);
        zoneRepository.delete(zone);
        log.info("Deleted restricted zone id {}", id);
    }

    private RestrictedZone findById(Long id) {
        return zoneRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("RestrictedZone", "id", id));
    }

    public ZoneResponse mapToResponse(RestrictedZone zone) {
        return ZoneResponse.builder()
                .id(zone.getId())
                .name(zone.getName())
                .description(zone.getDescription())
                .location(zone.getLocation())
                .boundaryData(zone.getBoundaryData())
                .active(zone.isActive())
                .createdAt(zone.getCreatedAt())
                .build();
    }
}

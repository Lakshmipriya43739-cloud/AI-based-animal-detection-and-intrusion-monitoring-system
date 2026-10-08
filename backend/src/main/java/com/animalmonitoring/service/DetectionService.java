package com.animalmonitoring.service;

import com.animalmonitoring.dto.response.DetectionResponse;
import com.animalmonitoring.entity.Detection;
import com.animalmonitoring.entity.RiskLevel;
import com.animalmonitoring.exception.ResourceNotFoundException;
import com.animalmonitoring.repository.DetectionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class DetectionService {

    private final DetectionRepository detectionRepository;

    public DetectionService(DetectionRepository detectionRepository) {
        this.detectionRepository = detectionRepository;
    }

    @Transactional(readOnly = true)
    public List<DetectionResponse> getAllDetections() {
        return detectionRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public DetectionResponse getDetectionById(Long id) {
        return mapToResponse(findById(id));
    }

    @Transactional(readOnly = true)
    public List<DetectionResponse> getIntrusionDetections() {
        return detectionRepository.findByIntrusionDetected(true).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<DetectionResponse> getDetectionsByRiskLevel(RiskLevel riskLevel) {
        return detectionRepository.findByRiskLevel(riskLevel).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<DetectionResponse> getHighRiskDetections() {
        return detectionRepository.findByRiskLevelIn(List.of(RiskLevel.HIGH, RiskLevel.CRITICAL)).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<DetectionResponse> getDetectionsByAnimal(Long animalId) {
        return detectionRepository.findByAnimalId(animalId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public com.animalmonitoring.dto.response.DetectionStatisticsResponse getStatistics() {
        long total = detectionRepository.count();
        long intrusions = detectionRepository.countByIntrusionDetectedTrue();
        long low = detectionRepository.countByRiskLevel(RiskLevel.LOW);
        long medium = detectionRepository.countByRiskLevel(RiskLevel.MEDIUM);
        long high = detectionRepository.countByRiskLevel(RiskLevel.HIGH);
        long critical = detectionRepository.countByRiskLevel(RiskLevel.CRITICAL);

        return com.animalmonitoring.dto.response.DetectionStatisticsResponse.builder()
                .totalDetections(total)
                .intrusionCount(intrusions)
                .lowRiskCount(low)
                .mediumRiskCount(medium)
                .highRiskCount(high)
                .criticalRiskCount(critical)
                .build();
    }

    @Transactional(readOnly = true)
    public List<DetectionResponse> getDetectionsByZone(Long zoneId) {
        return detectionRepository.findByZoneId(zoneId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private Detection findById(Long id) {
        return detectionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Detection", "id", id));
    }

    public DetectionResponse mapToResponse(Detection detection) {
        return DetectionResponse.builder()
                .id(detection.getId())
                .animalId(detection.getAnimal().getId())
                .animalName(detection.getAnimal().getName())
                .confidence(detection.getConfidence())
                .timestamp(detection.getTimestamp())
                .location(detection.getLocation())
                .intrusionDetected(detection.isIntrusionDetected())
                .riskLevel(detection.getRiskLevel())
                .imagePath(detection.getImagePath())
                .zoneId(detection.getZone() != null ? detection.getZone().getId() : null)
                .zoneName(detection.getZone() != null ? detection.getZone().getName() : null)
                .build();
    }
}

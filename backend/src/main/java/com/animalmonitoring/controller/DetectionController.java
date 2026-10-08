package com.animalmonitoring.controller;

import com.animalmonitoring.dto.response.ApiResponse;
import com.animalmonitoring.dto.response.DetectionResponse;
import com.animalmonitoring.entity.RiskLevel;
import com.animalmonitoring.service.DetectionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/detections")
public class DetectionController {

    private final DetectionService detectionService;

    public DetectionController(DetectionService detectionService) {
        this.detectionService = detectionService;
    }

    @GetMapping("/stats")
    public ResponseEntity<ApiResponse<com.animalmonitoring.dto.response.DetectionStatisticsResponse>> getStatistics() {
        return ResponseEntity.ok(ApiResponse.success(detectionService.getStatistics()));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<DetectionResponse>>> getAllDetections() {
        return ResponseEntity.ok(ApiResponse.success(detectionService.getAllDetections()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<DetectionResponse>> getDetectionById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(detectionService.getDetectionById(id)));
    }

    @GetMapping("/intrusions")
    public ResponseEntity<ApiResponse<List<DetectionResponse>>> getIntrusionDetections() {
        return ResponseEntity.ok(ApiResponse.success(detectionService.getIntrusionDetections()));
    }

    @GetMapping("/high-risk")
    public ResponseEntity<ApiResponse<List<DetectionResponse>>> getHighRiskDetections() {
        return ResponseEntity.ok(ApiResponse.success(detectionService.getHighRiskDetections()));
    }

    @GetMapping("/risk/{level}")
    public ResponseEntity<ApiResponse<List<DetectionResponse>>> getByRiskLevel(
            @PathVariable RiskLevel level) {
        return ResponseEntity.ok(ApiResponse.success(
                detectionService.getDetectionsByRiskLevel(level)));
    }

    @GetMapping("/animal/{animalId}")
    public ResponseEntity<ApiResponse<List<DetectionResponse>>> getByAnimal(
            @PathVariable Long animalId) {
        return ResponseEntity.ok(ApiResponse.success(
                detectionService.getDetectionsByAnimal(animalId)));
    }

    @GetMapping("/zone/{zoneId}")
    public ResponseEntity<ApiResponse<List<DetectionResponse>>> getByZone(
            @PathVariable Long zoneId) {
        return ResponseEntity.ok(ApiResponse.success(
                detectionService.getDetectionsByZone(zoneId)));
    }
}

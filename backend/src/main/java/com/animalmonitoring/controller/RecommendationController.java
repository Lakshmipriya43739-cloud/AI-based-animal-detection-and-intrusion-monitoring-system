package com.animalmonitoring.controller;

import com.animalmonitoring.dto.request.RecommendationRequest;
import com.animalmonitoring.dto.response.ApiResponse;
import com.animalmonitoring.dto.response.RecommendationResponse;
import com.animalmonitoring.service.RecommendationService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/recommendations")
public class RecommendationController {

    private final RecommendationService recommendationService;

    public RecommendationController(RecommendationService recommendationService) {
        this.recommendationService = recommendationService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<RecommendationResponse>>> getAllRecommendations() {
        return ResponseEntity.ok(ApiResponse.success(
                recommendationService.getAllRecommendations()));
    }

    @GetMapping("/active")
    public ResponseEntity<ApiResponse<List<RecommendationResponse>>> getActiveRecommendations() {
        return ResponseEntity.ok(ApiResponse.success(
                recommendationService.getActiveRecommendations()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<RecommendationResponse>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(
                recommendationService.getRecommendationById(id)));
    }

    @GetMapping("/animal/{animalId}")
    public ResponseEntity<ApiResponse<List<RecommendationResponse>>> getByAnimal(
            @PathVariable Long animalId) {
        return ResponseEntity.ok(ApiResponse.success(
                recommendationService.getRecommendationsByAnimal(animalId)));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<RecommendationResponse>> create(
            @Valid @RequestBody RecommendationRequest request) {
        RecommendationResponse created = recommendationService.createRecommendation(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Recommendation created successfully", created));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<RecommendationResponse>> update(
            @PathVariable Long id,
            @Valid @RequestBody RecommendationRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Recommendation updated successfully",
                recommendationService.updateRecommendation(id, request)));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        recommendationService.deleteRecommendation(id);
        return ResponseEntity.ok(ApiResponse.success("Recommendation deleted successfully", null));
    }
}

package com.animalmonitoring.service;

import com.animalmonitoring.dto.request.RecommendationRequest;
import com.animalmonitoring.dto.response.RecommendationResponse;
import com.animalmonitoring.entity.Animal;
import com.animalmonitoring.entity.Recommendation;
import com.animalmonitoring.exception.ResourceNotFoundException;
import com.animalmonitoring.repository.AnimalRepository;
import com.animalmonitoring.repository.RecommendationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class RecommendationService {

    private static final Logger log = LoggerFactory.getLogger(RecommendationService.class);

    private final RecommendationRepository recommendationRepository;
    private final AnimalRepository animalRepository;

    public RecommendationService(RecommendationRepository recommendationRepository,
                                 AnimalRepository animalRepository) {
        this.recommendationRepository = recommendationRepository;
        this.animalRepository = animalRepository;
    }

    @Transactional(readOnly = true)
    public List<RecommendationResponse> getAllRecommendations() {
        return recommendationRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<RecommendationResponse> getActiveRecommendations() {
        return recommendationRepository.findByActive(true).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public RecommendationResponse getRecommendationById(Long id) {
        return mapToResponse(findById(id));
    }

    @Transactional(readOnly = true)
    public List<RecommendationResponse> getRecommendationsByAnimal(Long animalId) {
        return recommendationRepository.findByAnimalId(animalId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public RecommendationResponse createRecommendation(RecommendationRequest request) {
        Animal animal = animalRepository.findById(request.getAnimalId())
                .orElseThrow(() -> new ResourceNotFoundException("Animal", "id", request.getAnimalId()));

        Recommendation rec = Recommendation.builder()
                .animal(animal)
                .riskLevel(request.getRiskLevel())
                .recommendation(request.getRecommendation())
                .active(request.isActive())
                .build();

        Recommendation saved = recommendationRepository.save(rec);
        log.info("Created recommendation id {} for animal '{}'", saved.getId(), animal.getName());
        return mapToResponse(saved);
    }

    @Transactional
    public RecommendationResponse updateRecommendation(Long id, RecommendationRequest request) {
        Recommendation rec = findById(id);

        Animal animal = animalRepository.findById(request.getAnimalId())
                .orElseThrow(() -> new ResourceNotFoundException("Animal", "id", request.getAnimalId()));

        rec.setAnimal(animal);
        rec.setRiskLevel(request.getRiskLevel());
        rec.setRecommendation(request.getRecommendation());
        rec.setActive(request.isActive());

        Recommendation saved = recommendationRepository.save(rec);
        log.info("Updated recommendation id {}", saved.getId());
        return mapToResponse(saved);
    }

    @Transactional
    public void deleteRecommendation(Long id) {
        Recommendation rec = findById(id);
        recommendationRepository.delete(rec);
        log.info("Deleted recommendation id {}", id);
    }

    private Recommendation findById(Long id) {
        return recommendationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Recommendation", "id", id));
    }

    public RecommendationResponse mapToResponse(Recommendation rec) {
        return RecommendationResponse.builder()
                .id(rec.getId())
                .animalId(rec.getAnimal().getId())
                .animalName(rec.getAnimal().getName())
                .riskLevel(rec.getRiskLevel())
                .recommendation(rec.getRecommendation())
                .active(rec.isActive())
                .build();
    }
}

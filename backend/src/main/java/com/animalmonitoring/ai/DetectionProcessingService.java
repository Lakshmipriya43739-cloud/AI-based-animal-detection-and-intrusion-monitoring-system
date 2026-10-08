package com.animalmonitoring.ai;

import com.animalmonitoring.entity.*;
import com.animalmonitoring.repository.AnimalRepository;
import com.animalmonitoring.repository.DetectionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Orchestrates the complete detection pipeline:
 *
 * <pre>
 * Image bytes
 *   → AnimalDetectionService (OpenCV pre-process + ONNX inference)
 *   → For each DetectionResult:
 *       → Animal lookup by name
 *       → IntrusionDetectionService (zone check)
 *       → RiskAssessmentService
 *       → ImageStorageService (save evidence if intruding)
 *       → Save Detection entity to MySQL
 * </pre>
 *
 * <p>This class is the single entry point for the REST controller.
 * It does not contain detection logic itself — it delegates to specialised services.
 */
@Service
public class DetectionProcessingService {

    private static final Logger log = LoggerFactory.getLogger(DetectionProcessingService.class);

    private final AnimalDetectionService   animalDetectionService;
    private final IntrusionDetectionService intrusionDetectionService;
    private final RiskAssessmentService    riskAssessmentService;
    private final ImageStorageService      imageStorageService;
    private final AnimalRepository         animalRepository;
    private final DetectionRepository      detectionRepository;

    public DetectionProcessingService(
            AnimalDetectionService animalDetectionService,
            IntrusionDetectionService intrusionDetectionService,
            RiskAssessmentService riskAssessmentService,
            ImageStorageService imageStorageService,
            AnimalRepository animalRepository,
            DetectionRepository detectionRepository) {

        this.animalDetectionService   = animalDetectionService;
        this.intrusionDetectionService = intrusionDetectionService;
        this.riskAssessmentService    = riskAssessmentService;
        this.imageStorageService      = imageStorageService;
        this.animalRepository         = animalRepository;
        this.detectionRepository      = detectionRepository;
    }

    /**
     * Process an uploaded image frame through the full AI pipeline and persist results.
     *
     * @param imageBytes Raw image bytes from the REST upload.
     * @param location   Optional location description string (e.g., "Camera-01, North Gate").
     * @return List of persisted {@link Detection} entities.
     */
    @Transactional
    public List<Detection> processImage(byte[] imageBytes, String location) {
        List<DetectionResult> aiResults = animalDetectionService.detectFromBytes(imageBytes);

        if (aiResults.isEmpty()) {
            log.debug("No animals detected in uploaded frame.");
            return List.of();
        }

        List<Detection> saved = new ArrayList<>();

        for (DetectionResult result : aiResults) {
            Optional<Detection> detection = buildAndSaveDetection(result, imageBytes, location);
            detection.ifPresent(saved::add);
        }

        log.info("Pipeline complete: {} detection(s) persisted from frame at '{}'",
                saved.size(), location);
        return saved;
    }

    // ------------------------------------------------------------------ private
    private Optional<Detection> buildAndSaveDetection(DetectionResult result,
                                                       byte[] imageBytes,
                                                       String location) {
        // 1. Resolve Animal entity by name
        Optional<Animal> animalOpt = animalRepository
                .findAll()
                .stream()
                .filter(a -> a.getName().equalsIgnoreCase(result.getAnimalName()))
                .findFirst();

        Animal animal;
        if (animalOpt.isPresent()) {
            animal = animalOpt.get();
        } else {
            animal = animalRepository.save(Animal.builder()
                    .name(result.getAnimalName())
                    .scientificName(result.getAnimalName())
                    .defaultRiskLevel(RiskLevel.MEDIUM)
                    .description("Auto-registered from YOLO11 detection")
                    .active(true)
                    .build());
            log.info("Auto-registered new animal class '{}' in database", result.getAnimalName());
        }

        // 2. Intrusion check
        Optional<RestrictedZone> intrudedZone =
                intrusionDetectionService.findIntrudedZone(result);
        boolean intruding = intrudedZone.isPresent();

        // 3. Risk assessment
        RiskLevel riskLevel = riskAssessmentService.assess(
                animal.getDefaultRiskLevel(),
                intruding,
                result.getConfidence());

        // 4. Save evidence image (only for intrusions to conserve disk space)
        String imagePath = null;
        if (intruding) {
            imagePath = imageStorageService.saveImageBytes(
                    imageBytes, result.getAnimalName());
        }

        // 5. Build and persist Detection entity
        Detection detection = Detection.builder()
                .animal(animal)
                .confidence((double) result.getConfidence())
                .timestamp(LocalDateTime.now())
                .location(location)
                .intrusionDetected(intruding)
                .riskLevel(riskLevel)
                .imagePath(imagePath)
                .zone(intrudedZone.orElse(null))
                .build();

        Detection persisted = detectionRepository.save(detection);
        log.debug("Saved detection id={} animal='{}' risk={} intrusion={}",
                persisted.getId(), animal.getName(), riskLevel, intruding);

        return Optional.of(persisted);
    }
}

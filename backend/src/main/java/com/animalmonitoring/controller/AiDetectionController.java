package com.animalmonitoring.controller;

import com.animalmonitoring.ai.*;
import com.animalmonitoring.dto.response.ApiResponse;
import com.animalmonitoring.dto.response.DetectResponse;
import com.animalmonitoring.dto.response.DetectionItemResponse;
import com.animalmonitoring.entity.Detection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * REST endpoint for submitting images/frames to the AI detection pipeline.
 *
 * <h3>Authentication</h3>
 * Secured — all requests require a valid JWT Bearer token.
 *
 * <h3>POST /api/ai/detect</h3>
 * Accepts a multipart image upload, runs the full detection pipeline, and
 * returns a structured JSON response.
 */
@RestController
@RequestMapping("/api/ai")
public class AiDetectionController {

    private static final Logger log = LoggerFactory.getLogger(AiDetectionController.class);

    private final DetectionProcessingService detectionProcessingService;
    private final AnimalDetectionService     animalDetectionService;

    public AiDetectionController(
            DetectionProcessingService detectionProcessingService,
            AnimalDetectionService animalDetectionService) {
        this.detectionProcessingService = detectionProcessingService;
        this.animalDetectionService     = animalDetectionService;
    }

    /**
     * POST /api/ai/detect
     *
     * <p>Upload an image file (JPEG/PNG) and receive detection results.
     *
     * @param file      Multipart image file.
     * @param location  Optional camera/location label (form parameter).
     */
    @PostMapping(value = "/detect", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<DetectResponse>> detect(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "location", required = false,
                          defaultValue = "Unknown") String location) {

        if (file.isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Uploaded file is empty"));
        }

        byte[] imageBytes;
        try {
            imageBytes = file.getBytes();
        } catch (IOException e) {
            log.error("Failed to read uploaded file: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.error("Could not read uploaded file"));
        }

        List<Detection> persisted = detectionProcessingService.processImage(imageBytes, location);

        List<DetectionItemResponse> items = new ArrayList<>();
        for (Detection d : persisted) {
            items.add(DetectionItemResponse.builder()
                    .detectionId(d.getId())
                    .animalName(d.getAnimal().getName())
                    .confidence(d.getConfidence().floatValue())
                    .intrusionDetected(d.isIntrusionDetected())
                    .riskLevel(d.getRiskLevel())
                    .zoneName(d.getZone() != null ? d.getZone().getName() : null)
                    .build());
        }

        String note = animalDetectionService.isModelReady()
                ? null
                : "AI model not loaded (stub mode). Place yolov8n.onnx in models/ and restart.";

        DetectResponse body = DetectResponse.builder()
                .timestamp(LocalDateTime.now())
                .location(location)
                .modelReady(animalDetectionService.isModelReady())
                .note(note)
                .detections(items)
                .build();

        return ResponseEntity.ok(ApiResponse.success(body));
    }

    /**
     * GET /api/ai/status
     * Quick health check — returns whether the ONNX model is loaded.
     */
    @GetMapping("/status")
    public ResponseEntity<ApiResponse<String>> status() {
        String msg = animalDetectionService.isModelReady()
                ? "AI model is loaded and ready for inference."
                : "AI model not loaded. Running in stub mode. "
                + "Place the ONNX model file at the configured path and restart.";
        return ResponseEntity.ok(ApiResponse.success(msg));
    }
}

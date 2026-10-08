package com.animalmonitoring.ai;

import org.opencv.core.Mat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

/**
 * High-level animal detection service.
 *
 * Coordinates the OpenCV pre-processing step and the ONNX inference step.
 * Callers use this class rather than {@link OnnxModelService} or {@link FrameProcessor} directly.
 *
 * <p>Usage pattern:
 * <pre>
 *   byte[] imageBytes = multipartFile.getBytes();
 *   Mat frame = frameProcessor.decodeImageBytes(imageBytes);
 *   List&lt;DetectionResult&gt; results = animalDetectionService.detectFromMat(frame);
 *   frame.release();
 * </pre>
 */
@Service
public class AnimalDetectionService {

    private static final Logger log = LoggerFactory.getLogger(AnimalDetectionService.class);

    private final OnnxModelService onnxModelService;
    private final FrameProcessor   frameProcessor;

    public AnimalDetectionService(OnnxModelService onnxModelService,
                                  FrameProcessor frameProcessor) {
        this.onnxModelService = onnxModelService;
        this.frameProcessor   = frameProcessor;
    }

    /**
     * Run detection on raw image bytes (e.g., from a REST multipart upload).
     *
     * @param imageBytes  JPEG, PNG, or BMP encoded bytes.
     * @return List of {@link DetectionResult} objects after filtering and NMS.
     */
    public List<DetectionResult> detectFromBytes(byte[] imageBytes) {
        if (imageBytes == null || imageBytes.length == 0) {
            log.warn("detectFromBytes: received null or empty image bytes");
            return Collections.emptyList();
        }

        Mat frame = frameProcessor.decodeImageBytes(imageBytes);
        try {
            return detectFromMat(frame);
        } finally {
            frame.release();
        }
    }

    /**
     * Run detection on a filesystem image file.
     *
     * @param filePath Absolute or relative path to the image.
     */
    public List<DetectionResult> detectFromFile(String filePath) {
        Mat frame = frameProcessor.loadImageFromPath(filePath);
        try {
            return detectFromMat(frame);
        } finally {
            frame.release();
        }
    }

    /**
     * Run detection on an already-decoded OpenCV {@link Mat}.
     * The caller is responsible for releasing the Mat after this call.
     *
     * @param frame BGR image (standard OpenCV output).
     * @return Detection results.
     */
    public List<DetectionResult> detectFromMat(Mat frame) {
        if (frame == null || frame.empty()) {
            log.warn("detectFromMat: received null or empty Mat");
            return Collections.emptyList();
        }

        int origWidth  = frameProcessor.getWidth(frame);
        int origHeight = frameProcessor.getHeight(frame);

        float[] inputData = frameProcessor.preprocessForYolo(frame);
        List<DetectionResult> results =
                onnxModelService.runInference(inputData, origWidth, origHeight);

        log.debug("Detection complete: {} animal(s) found in {}x{} image",
                results.size(), origWidth, origHeight);
        return results;
    }

    /** Whether the underlying ONNX model is loaded and ready for real inference. */
    public boolean isModelReady() {
        return onnxModelService.isModelLoaded();
    }
}

package com.animalmonitoring.ai;

import ai.onnxruntime.OnnxTensor;
import ai.onnxruntime.OrtEnvironment;
import ai.onnxruntime.OrtException;
import ai.onnxruntime.OrtSession;
import jakarta.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.File;
import java.nio.FloatBuffer;
import java.util.*;

/**
 * Wraps the ONNX Runtime session for YOLO object detection.
 *
 * <h3>Model placement</h3>
 * Place the exported YOLO ONNX file at the path configured in {@code ai.model.path}
 * (default: {@code backend/models/yolov8n.onnx}).  If the file is absent at startup,
 * the service operates in <em>stub mode</em> — all inference calls return an empty list
 * and a warning is logged.  This preserves application startup during development.
 *
 * <h3>Model output layout (YOLOv8 exported via ultralytics)</h3>
 * The expected output tensor shape is {@code [1, num_classes+4, num_boxes]} where:
 * <ul>
 *   <li>Rows 0-3: cx, cy, w, h (normalised 0–1)</li>
 *   <li>Rows 4+:  class confidence scores</li>
 * </ul>
 * If your model has a different layout, adjust {@link #parseYoloOutput} accordingly.
 *
 * <h3>Thread safety</h3>
 * {@link OrtSession} is thread-safe for concurrent inference calls.
 * The session is created once at startup and reused for all requests.
 */
@Service
public class OnnxModelService {

    private static final Logger log = LoggerFactory.getLogger(OnnxModelService.class);

    private final AiModelConfiguration config;

    private OrtEnvironment ortEnvironment;
    private OrtSession    ortSession;

    /** True when the model file was found and loaded successfully. */
    private boolean modelLoaded = false;

    public OnnxModelService(AiModelConfiguration config) {
        this.config = config;
        initModel();
    }

    // ------------------------------------------------------------------ initialisation
    private void initModel() {
        String modelPath = config.getModel().getPath();
        File modelFile = new File(modelPath);

        if (!modelFile.exists()) {
            log.warn("ONNX model file not found at '{}'. AI service is running in STUB mode. "
                    + "Place your YOLOv8 .onnx file at that path and restart to enable inference.",
                    modelFile.getAbsolutePath());
            return;
        }

        try {
            ortEnvironment = OrtEnvironment.getEnvironment();
            OrtSession.SessionOptions opts = new OrtSession.SessionOptions();
            opts.setOptimizationLevel(OrtSession.SessionOptions.OptLevel.ALL_OPT);
            ortSession  = ortEnvironment.createSession(modelFile.getAbsolutePath(), opts);
            modelLoaded = true;
            log.info("ONNX model loaded successfully from '{}'. Input names: {}",
                    modelFile.getAbsolutePath(), ortSession.getInputNames());
        } catch (OrtException e) {
            log.error("Failed to load ONNX model from '{}': {}", modelPath, e.getMessage(), e);
        }
    }

    // ------------------------------------------------------------------ inference
    /**
     * Run inference on a pre-processed float array.
     *
     * @param inputData  Normalised float array of shape [1, 3, H, W] in CHW order.
     * @param origWidth  Original image width (pixels) — used to scale boxes back.
     * @param origHeight Original image height (pixels) — used to scale boxes back.
     * @return List of {@link DetectionResult} after confidence filtering and NMS.
     *         Returns empty list when running in stub mode or on error.
     */
    public List<DetectionResult> runInference(float[] inputData, int origWidth, int origHeight) {
        if (!modelLoaded) {
            log.debug("Model not loaded (stub mode). Returning empty detections.");
            return Collections.emptyList();
        }

        int inputW = config.getInput().getWidth();
        int inputH = config.getInput().getHeight();

        try {
            long[] inputShape = {1L, 3L, inputH, inputW};
            OnnxTensor inputTensor = OnnxTensor.createTensor(
                    ortEnvironment, FloatBuffer.wrap(inputData), inputShape);

            String inputName = ortSession.getInputNames().iterator().next();
            Map<String, OnnxTensor> inputs = Map.of(inputName, inputTensor);

            try (OrtSession.Result result = ortSession.run(inputs)) {
                float[][][] rawOutput = (float[][][]) result.get(0).getValue();
                List<DetectionResult> detections =
                        parseYoloOutput(rawOutput, origWidth, origHeight);
                inputTensor.close();
                return detections;
            }
        } catch (OrtException e) {
            log.error("ONNX inference error: {}", e.getMessage(), e);
            return Collections.emptyList();
        }
    }

    // ------------------------------------------------------------------ output parsing
    /**
     * Parses YOLOv8 output tensor into DetectionResult objects.
     *
     * <p>YOLOv8 exports with shape [1, (4 + num_classes), num_boxes] where:
     * <ul>
     *   <li>rawOutput[0][0..3][box] = cx, cy, w, h (normalised 0–1)</li>
     *   <li>rawOutput[0][4..end][box] = per-class confidence scores</li>
     * </ul>
     *
     * @param rawOutput  Raw ONNX tensor value after getValue()
     * @param origWidth  Image width for scaling boxes
     * @param origHeight Image height for scaling boxes
     */
    private List<DetectionResult> parseYoloOutput(float[][][] rawOutput,
                                                   int origWidth, int origHeight) {
        float confThreshold = config.getConfidence().getThreshold();
        int numRows = rawOutput[0].length;       // 4 + numClasses
        int numBoxes = rawOutput[0][0].length;
        int numClasses = numRows - 4;

        if (numClasses <= 0) {
            log.warn("Unexpected model output shape: {} rows, {} boxes", numRows, numBoxes);
            return Collections.emptyList();
        }

        List<DetectionResult> candidates = new ArrayList<>();

        for (int b = 0; b < numBoxes; b++) {
            // Find best class
            int   bestClass = -1;
            float bestScore = 0.0f;
            for (int c = 0; c < numClasses; c++) {
                float score = rawOutput[0][4 + c][b];
                if (score > bestScore) {
                    bestScore = score;
                    bestClass = c;
                }
            }

            if (bestScore < confThreshold) continue;

            int inputW = config.getInput().getWidth();
            int inputH = config.getInput().getHeight();

            float rawCx = rawOutput[0][0][b];
            float rawCy = rawOutput[0][1][b];
            float rawBw = rawOutput[0][2][b];
            float rawBh = rawOutput[0][3][b];
            if (candidates.isEmpty()) {
    log.info("YOLO raw box: cx={}, cy={}, w={}, h={}, class={}, confidence={}",
            rawCx, rawCy, rawBw, rawBh, bestClass, bestScore);
}

            // Scale coordinates from model input size (e.g. 640x640) to original image dimensions
            float cx = (rawCx > 1.0f ? rawCx / inputW : rawCx) * origWidth;
            float cy = (rawCy > 1.0f ? rawCy / inputH : rawCy) * origHeight;
            float bw = (rawBw > 1.0f ? rawBw / inputW : rawBw) * origWidth;
            float bh = (rawBh > 1.0f ? rawBh / inputH : rawBh) * origHeight;

            float x = cx - bw / 2.0f;
            float y = cy - bh / 2.0f;

            String animalName = config.resolveClassName(bestClass);
            BoundingBox box   = new BoundingBox(x, y, bw, bh);
            candidates.add(new DetectionResult(animalName, bestClass, bestScore, box));
        }

        return applyNms(candidates);
    }

    /**
     * Non-Maximum Suppression — removes redundant overlapping boxes.
     * Boxes are sorted by confidence (descending) and suppressed when IoU
     * exceeds the configured NMS threshold.
     */
    private List<DetectionResult> applyNms(List<DetectionResult> detections) {
        if (detections.isEmpty()) return detections;

        float nmsThreshold = config.getNms().getThreshold();

        // Sort by confidence descending
        detections.sort((a, b) -> Float.compare(b.getConfidence(), a.getConfidence()));

        boolean[] suppressed = new boolean[detections.size()];
        List<DetectionResult> kept = new ArrayList<>();

        for (int i = 0; i < detections.size(); i++) {
            if (suppressed[i]) continue;
            DetectionResult current = detections.get(i);
            kept.add(current);

            for (int j = i + 1; j < detections.size(); j++) {
                if (suppressed[j]) continue;
                // Only suppress same-class overlapping boxes
                if (current.getClassId() == detections.get(j).getClassId()) {
                    float iou = current.getBoundingBox().iou(detections.get(j).getBoundingBox());
                    if (iou > nmsThreshold) {
                        suppressed[j] = true;
                    }
                }
            }
        }

        return kept;
    }

    // ------------------------------------------------------------------ lifecycle
    @PreDestroy
    public void cleanup() {
        try {
            if (ortSession != null)     ortSession.close();
            if (ortEnvironment != null) ortEnvironment.close();
            log.info("ONNX session closed.");
        } catch (OrtException e) {
            log.warn("Error closing ONNX session: {}", e.getMessage());
        }
    }

    public boolean isModelLoaded() { return modelLoaded; }
}

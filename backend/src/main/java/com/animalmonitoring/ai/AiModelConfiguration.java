package com.animalmonitoring.ai;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Binds all AI-related properties from application.yml / environment variables.
 *
 * Example properties:
 *   ai.model.path              = models/yolov8n.onnx
 *   ai.confidence.threshold    = 0.5
 *   ai.nms.threshold           = 0.45
 *   ai.input.width             = 640
 *   ai.input.height            = 640
 *   ai.image.storage-path      = uploads/detections
 *   ai.class-names.0           = Elephant
 *   ai.class-names.1           = Tiger
 */
@Component
@ConfigurationProperties(prefix = "ai")
public class AiModelConfiguration {

    private Model model = new Model();
    private Confidence confidence = new Confidence();
    private Nms nms = new Nms();
    private Input input = new Input();
    private Image image = new Image();

    /**
     * Map of YOLO class index (Integer) → animal name (String).
     * Configured via ai.class-names.0=Elephant, ai.class-names.1=Tiger, etc.
     */
    private Map<Integer, String> classNames = new LinkedHashMap<>();

    // ------------------------------------------------------------------ inner classes
    public static class Model {
        private String path = "models/yolo11n.onnx";
        public String getPath()           { return path; }
        public void   setPath(String p)   { this.path = p; }
    }

    public static class Confidence {
        private float threshold = 0.5f;
        public float  getThreshold()          { return threshold; }
        public void   setThreshold(float t)   { this.threshold = t; }
    }

    public static class Nms {
        private float threshold = 0.45f;
        public float  getThreshold()          { return threshold; }
        public void   setThreshold(float t)   { this.threshold = t; }
    }

    public static class Input {
        private int width  = 640;
        private int height = 640;
        public int  getWidth()             { return width; }
        public void setWidth(int w)        { this.width = w; }
        public int  getHeight()            { return height; }
        public void setHeight(int h)       { this.height = h; }
    }

    public static class Image {
        private String storagePath = "uploads/detections";
        public String getStoragePath()           { return storagePath; }
        public void   setStoragePath(String p)   { this.storagePath = p; }
    }

    // ------------------------------------------------------------------ getters/setters
    public Model       getModel()              { return model; }
    public void        setModel(Model m)       { this.model = m; }
    public Confidence  getConfidence()         { return confidence; }
    public void        setConfidence(Confidence c) { this.confidence = c; }
    public Nms         getNms()                { return nms; }
    public void        setNms(Nms n)           { this.nms = n; }
    public Input       getInput()              { return input; }
    public void        setInput(Input i)       { this.input = i; }
    public Image       getImage()              { return image; }
    public void        setImage(Image i)       { this.image = i; }
    public Map<Integer, String> getClassNames()          { return classNames; }
    public void setClassNames(Map<Integer, String> map)  { this.classNames = map; }

    /**
     * Convenience: look up animal name by class ID.
     * Returns "Unknown" if the class ID is not in the configured map.
     */
    public String resolveClassName(int classId) {
        return classNames.getOrDefault(classId, "Unknown");
    }
}

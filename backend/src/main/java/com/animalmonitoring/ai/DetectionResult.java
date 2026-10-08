package com.animalmonitoring.ai;

/**
 * Single object-detection result produced by the YOLO inference pipeline.
 * One instance represents one detected animal in one image frame.
 */
public class DetectionResult {

    /** Animal name resolved from the model class index (e.g., "Elephant"). */
    private final String animalName;

    /** Model class index as produced by YOLO output (0-based). */
    private final int classId;

    /** Detection confidence score in range [0.0, 1.0]. */
    private final float confidence;

    /** Bounding box in original-image pixel coordinates. */
    private final BoundingBox boundingBox;

    public DetectionResult(String animalName, int classId,
                           float confidence, BoundingBox boundingBox) {
        this.animalName  = animalName;
        this.classId     = classId;
        this.confidence  = confidence;
        this.boundingBox = boundingBox;
    }

    public String getAnimalName()      { return animalName; }
    public int getClassId()            { return classId; }
    public float getConfidence()       { return confidence; }
    public BoundingBox getBoundingBox(){ return boundingBox; }

    @Override
    public String toString() {
        return String.format("DetectionResult{animal='%s', classId=%d, confidence=%.3f, box=%s}",
                animalName, classId, confidence, boundingBox);
    }
}

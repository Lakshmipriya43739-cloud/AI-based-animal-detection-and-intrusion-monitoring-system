package com.animalmonitoring.dto.response;

import com.animalmonitoring.entity.RiskLevel;

/**
 * One detected animal within a {@link DetectResponse}.
 */
public class DetectionItemResponse {
    private String animalName;
    private float  confidence;
    private float  boxX;
    private float  boxY;
    private float  boxWidth;
    private float  boxHeight;
    private boolean intrusionDetected;
    private RiskLevel riskLevel;
    private String zoneName;
    private Long detectionId;

    public DetectionItemResponse() {}

    private DetectionItemResponse(Builder b) {
        this.animalName       = b.animalName;
        this.confidence       = b.confidence;
        this.boxX             = b.boxX;
        this.boxY             = b.boxY;
        this.boxWidth         = b.boxWidth;
        this.boxHeight        = b.boxHeight;
        this.intrusionDetected = b.intrusionDetected;
        this.riskLevel        = b.riskLevel;
        this.zoneName         = b.zoneName;
        this.detectionId      = b.detectionId;
    }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private String animalName; private float confidence;
        private float boxX, boxY, boxWidth, boxHeight;
        private boolean intrusionDetected; private RiskLevel riskLevel;
        private String zoneName; private Long detectionId;

        public Builder animalName(String v)          { animalName = v; return this; }
        public Builder confidence(float v)           { confidence = v; return this; }
        public Builder boxX(float v)                 { boxX = v; return this; }
        public Builder boxY(float v)                 { boxY = v; return this; }
        public Builder boxWidth(float v)             { boxWidth = v; return this; }
        public Builder boxHeight(float v)            { boxHeight = v; return this; }
        public Builder intrusionDetected(boolean v)  { intrusionDetected = v; return this; }
        public Builder riskLevel(RiskLevel v)        { riskLevel = v; return this; }
        public Builder zoneName(String v)            { zoneName = v; return this; }
        public Builder detectionId(Long v)           { detectionId = v; return this; }
        public DetectionItemResponse build()         { return new DetectionItemResponse(this); }
    }

    public String getAnimalName()        { return animalName; }
    public float  getConfidence()        { return confidence; }
    public float  getBoxX()              { return boxX; }
    public float  getBoxY()              { return boxY; }
    public float  getBoxWidth()          { return boxWidth; }
    public float  getBoxHeight()         { return boxHeight; }
    public boolean isIntrusionDetected() { return intrusionDetected; }
    public RiskLevel getRiskLevel()      { return riskLevel; }
    public String getZoneName()          { return zoneName; }
    public Long getDetectionId()         { return detectionId; }
}

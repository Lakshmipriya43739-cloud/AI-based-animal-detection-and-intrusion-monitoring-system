package com.animalmonitoring.dto.response;

import com.animalmonitoring.entity.RiskLevel;
import java.time.LocalDateTime;

public class DetectionResponse {
    private Long id, animalId, zoneId;
    private String animalName, location, imagePath, zoneName;
    private Double confidence;
    private LocalDateTime timestamp;
    private boolean intrusionDetected;
    private RiskLevel riskLevel;

    public DetectionResponse() {}

    private DetectionResponse(Builder b) {
        this.id = b.id; this.animalId = b.animalId; this.animalName = b.animalName;
        this.confidence = b.confidence; this.timestamp = b.timestamp; this.location = b.location;
        this.intrusionDetected = b.intrusionDetected; this.riskLevel = b.riskLevel;
        this.imagePath = b.imagePath; this.zoneId = b.zoneId; this.zoneName = b.zoneName;
    }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private Long id, animalId, zoneId; private String animalName, location, imagePath, zoneName;
        private Double confidence; private LocalDateTime timestamp;
        private boolean intrusionDetected; private RiskLevel riskLevel;
        public Builder id(Long v)                  { id = v; return this; }
        public Builder animalId(Long v)            { animalId = v; return this; }
        public Builder animalName(String v)        { animalName = v; return this; }
        public Builder confidence(Double v)        { confidence = v; return this; }
        public Builder timestamp(LocalDateTime v)  { timestamp = v; return this; }
        public Builder location(String v)          { location = v; return this; }
        public Builder intrusionDetected(boolean v){ intrusionDetected = v; return this; }
        public Builder riskLevel(RiskLevel v)      { riskLevel = v; return this; }
        public Builder imagePath(String v)         { imagePath = v; return this; }
        public Builder zoneId(Long v)              { zoneId = v; return this; }
        public Builder zoneName(String v)          { zoneName = v; return this; }
        public DetectionResponse build()           { return new DetectionResponse(this); }
    }

    public Long getId()                  { return id; }
    public Long getAnimalId()            { return animalId; }
    public String getAnimalName()        { return animalName; }
    public Double getConfidence()        { return confidence; }
    public LocalDateTime getTimestamp()  { return timestamp; }
    public String getLocation()          { return location; }
    public boolean isIntrusionDetected() { return intrusionDetected; }
    public RiskLevel getRiskLevel()      { return riskLevel; }
    public String getImagePath()         { return imagePath; }
    public Long getZoneId()              { return zoneId; }
    public String getZoneName()          { return zoneName; }
}

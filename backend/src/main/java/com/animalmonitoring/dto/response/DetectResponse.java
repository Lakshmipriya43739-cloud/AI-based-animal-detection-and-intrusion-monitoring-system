package com.animalmonitoring.dto.response;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Top-level response body for POST /api/ai/detect.
 */
public class DetectResponse {
    private LocalDateTime timestamp;
    private String        location;
    private boolean       modelReady;
    private String        note;
    private List<DetectionItemResponse> detections;

    public DetectResponse() {}

    private DetectResponse(Builder b) {
        this.timestamp  = b.timestamp;
        this.location   = b.location;
        this.modelReady = b.modelReady;
        this.note       = b.note;
        this.detections = b.detections;
    }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private LocalDateTime timestamp = LocalDateTime.now();
        private String location; private boolean modelReady;
        private String note; private List<DetectionItemResponse> detections;

        public Builder timestamp(LocalDateTime v)                { timestamp = v; return this; }
        public Builder location(String v)                        { location = v; return this; }
        public Builder modelReady(boolean v)                     { modelReady = v; return this; }
        public Builder note(String v)                            { note = v; return this; }
        public Builder detections(List<DetectionItemResponse> v) { detections = v; return this; }
        public DetectResponse build()                            { return new DetectResponse(this); }
    }

    public LocalDateTime getTimestamp()                    { return timestamp; }
    public String getLocation()                            { return location; }
    public boolean isModelReady()                          { return modelReady; }
    public String getNote()                                { return note; }
    public List<DetectionItemResponse> getDetections()     { return detections; }
}

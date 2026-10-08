package com.animalmonitoring.dto.response;

public class DetectionStatisticsResponse {
    private long totalDetections;
    private long intrusionCount;
    private long lowRiskCount;
    private long mediumRiskCount;
    private long highRiskCount;
    private long criticalRiskCount;

    public DetectionStatisticsResponse() {}

    private DetectionStatisticsResponse(Builder b) {
        this.totalDetections  = b.totalDetections;
        this.intrusionCount   = b.intrusionCount;
        this.lowRiskCount     = b.lowRiskCount;
        this.mediumRiskCount  = b.mediumRiskCount;
        this.highRiskCount    = b.highRiskCount;
        this.criticalRiskCount = b.criticalRiskCount;
    }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private long totalDetections, intrusionCount;
        private long lowRiskCount, mediumRiskCount, highRiskCount, criticalRiskCount;

        public Builder totalDetections(long v)   { totalDetections = v; return this; }
        public Builder intrusionCount(long v)    { intrusionCount = v; return this; }
        public Builder lowRiskCount(long v)      { lowRiskCount = v; return this; }
        public Builder mediumRiskCount(long v)   { mediumRiskCount = v; return this; }
        public Builder highRiskCount(long v)     { highRiskCount = v; return this; }
        public Builder criticalRiskCount(long v) { criticalRiskCount = v; return this; }
        public DetectionStatisticsResponse build() { return new DetectionStatisticsResponse(this); }
    }

    public long getTotalDetections()   { return totalDetections; }
    public long getIntrusionCount()    { return intrusionCount; }
    public long getLowRiskCount()      { return lowRiskCount; }
    public long getMediumRiskCount()   { return mediumRiskCount; }
    public long getHighRiskCount()     { return highRiskCount; }
    public long getCriticalRiskCount() { return criticalRiskCount; }
}

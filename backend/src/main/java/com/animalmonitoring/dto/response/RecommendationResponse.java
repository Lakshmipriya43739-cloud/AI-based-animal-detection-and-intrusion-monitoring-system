package com.animalmonitoring.dto.response;

import com.animalmonitoring.entity.RiskLevel;

public class RecommendationResponse {
    private Long id, animalId;
    private String animalName, recommendation;
    private RiskLevel riskLevel;
    private boolean active;

    public RecommendationResponse() {}

    private RecommendationResponse(Builder b) {
        this.id = b.id; this.animalId = b.animalId; this.animalName = b.animalName;
        this.riskLevel = b.riskLevel; this.recommendation = b.recommendation; this.active = b.active;
    }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private Long id, animalId; private String animalName, recommendation;
        private RiskLevel riskLevel; private boolean active;
        public Builder id(Long v)                { id = v; return this; }
        public Builder animalId(Long v)          { animalId = v; return this; }
        public Builder animalName(String v)      { animalName = v; return this; }
        public Builder riskLevel(RiskLevel v)    { riskLevel = v; return this; }
        public Builder recommendation(String v)  { recommendation = v; return this; }
        public Builder active(boolean v)         { active = v; return this; }
        public RecommendationResponse build()    { return new RecommendationResponse(this); }
    }

    public Long getId()                { return id; }
    public Long getAnimalId()          { return animalId; }
    public String getAnimalName()      { return animalName; }
    public RiskLevel getRiskLevel()    { return riskLevel; }
    public String getRecommendation()  { return recommendation; }
    public boolean isActive()          { return active; }
}

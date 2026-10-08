package com.animalmonitoring.dto.response;

import com.animalmonitoring.entity.RiskLevel;

public class AnimalResponse {
    private Long id;
    private String name, scientificName, description;
    private RiskLevel defaultRiskLevel;
    private boolean active;

    public AnimalResponse() {}

    private AnimalResponse(Builder b) {
        this.id = b.id; this.name = b.name; this.scientificName = b.scientificName;
        this.defaultRiskLevel = b.defaultRiskLevel; this.description = b.description; this.active = b.active;
    }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private Long id; private String name, scientificName, description;
        private RiskLevel defaultRiskLevel; private boolean active;
        public Builder id(Long v)                  { id = v; return this; }
        public Builder name(String v)              { name = v; return this; }
        public Builder scientificName(String v)    { scientificName = v; return this; }
        public Builder defaultRiskLevel(RiskLevel v){ defaultRiskLevel = v; return this; }
        public Builder description(String v)       { description = v; return this; }
        public Builder active(boolean v)           { active = v; return this; }
        public AnimalResponse build()              { return new AnimalResponse(this); }
    }

    public Long getId()                  { return id; }
    public String getName()              { return name; }
    public String getScientificName()    { return scientificName; }
    public RiskLevel getDefaultRiskLevel(){ return defaultRiskLevel; }
    public String getDescription()       { return description; }
    public boolean isActive()            { return active; }
}

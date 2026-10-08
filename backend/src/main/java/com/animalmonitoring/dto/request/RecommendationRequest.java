package com.animalmonitoring.dto.request;

import com.animalmonitoring.entity.RiskLevel;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class RecommendationRequest {

    @NotNull(message = "Animal ID is required")
    private Long animalId;

    @NotNull(message = "Risk level is required")
    private RiskLevel riskLevel;

    @NotBlank(message = "Recommendation text is required")
    private String recommendation;

    private boolean active = true;

    public RecommendationRequest() {}

    public Long getAnimalId()                    { return animalId; }
    public void setAnimalId(Long animalId)       { this.animalId = animalId; }
    public RiskLevel getRiskLevel()              { return riskLevel; }
    public void setRiskLevel(RiskLevel r)        { this.riskLevel = r; }
    public String getRecommendation()            { return recommendation; }
    public void setRecommendation(String r)      { this.recommendation = r; }
    public boolean isActive()                    { return active; }
    public void setActive(boolean active)        { this.active = active; }
}

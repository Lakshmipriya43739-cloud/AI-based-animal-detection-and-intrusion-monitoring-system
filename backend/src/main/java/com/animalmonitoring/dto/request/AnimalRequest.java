package com.animalmonitoring.dto.request;

import com.animalmonitoring.entity.RiskLevel;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class AnimalRequest {

    @NotBlank(message = "Animal name is required")
    @Size(max = 100)
    private String name;

    @Size(max = 150)
    private String scientificName;

    @NotNull(message = "Default risk level is required")
    private RiskLevel defaultRiskLevel;

    private String description;

    private boolean active = true;

    public AnimalRequest() {}

    public String getName()                        { return name; }
    public void setName(String name)               { this.name = name; }
    public String getScientificName()              { return scientificName; }
    public void setScientificName(String s)        { this.scientificName = s; }
    public RiskLevel getDefaultRiskLevel()         { return defaultRiskLevel; }
    public void setDefaultRiskLevel(RiskLevel r)   { this.defaultRiskLevel = r; }
    public String getDescription()                 { return description; }
    public void setDescription(String d)           { this.description = d; }
    public boolean isActive()                      { return active; }
    public void setActive(boolean active)          { this.active = active; }
}

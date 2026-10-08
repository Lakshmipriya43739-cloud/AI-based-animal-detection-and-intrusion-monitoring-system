package com.animalmonitoring.entity;

import jakarta.persistence.*;

import java.util.List;

/**
 * Master record for a type of animal that can be detected by the system.
 */
@Entity
@Table(name = "animals")
public class Animal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(name = "scientific_name")
    private String scientificName;

    @Enumerated(EnumType.STRING)
    @Column(name = "default_risk_level", nullable = false)
    private RiskLevel defaultRiskLevel;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private boolean active = true;

    @OneToMany(mappedBy = "animal", fetch = FetchType.LAZY)
    private List<Detection> detections;

    @OneToMany(mappedBy = "animal", fetch = FetchType.LAZY)
    private List<Recommendation> recommendations;

    public Animal() {}

    private Animal(Builder builder) {
        this.id = builder.id;
        this.name = builder.name;
        this.scientificName = builder.scientificName;
        this.defaultRiskLevel = builder.defaultRiskLevel;
        this.description = builder.description;
        this.active = builder.active;
    }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private Long id;
        private String name;
        private String scientificName;
        private RiskLevel defaultRiskLevel;
        private String description;
        private boolean active = true;

        public Builder id(Long id)                          { this.id = id; return this; }
        public Builder name(String name)                    { this.name = name; return this; }
        public Builder scientificName(String s)             { this.scientificName = s; return this; }
        public Builder defaultRiskLevel(RiskLevel r)        { this.defaultRiskLevel = r; return this; }
        public Builder description(String d)                { this.description = d; return this; }
        public Builder active(boolean active)               { this.active = active; return this; }
        public Animal build()                               { return new Animal(this); }
    }

    public Long getId()                               { return id; }
    public void setId(Long id)                        { this.id = id; }
    public String getName()                           { return name; }
    public void setName(String name)                  { this.name = name; }
    public String getScientificName()                 { return scientificName; }
    public void setScientificName(String s)           { this.scientificName = s; }
    public RiskLevel getDefaultRiskLevel()            { return defaultRiskLevel; }
    public void setDefaultRiskLevel(RiskLevel r)      { this.defaultRiskLevel = r; }
    public String getDescription()                    { return description; }
    public void setDescription(String d)              { this.description = d; }
    public boolean isActive()                         { return active; }
    public void setActive(boolean active)             { this.active = active; }
    public List<Detection> getDetections()            { return detections; }
    public void setDetections(List<Detection> d)      { this.detections = d; }
    public List<Recommendation> getRecommendations()  { return recommendations; }
    public void setRecommendations(List<Recommendation> r) { this.recommendations = r; }
}

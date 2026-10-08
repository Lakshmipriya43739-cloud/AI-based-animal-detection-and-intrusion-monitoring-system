package com.animalmonitoring.entity;

import jakarta.persistence.*;

/**
 * Safety recommendation for a specific animal/risk-level combination.
 */
@Entity
@Table(name = "recommendations")
public class Recommendation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "animal_id", nullable = false)
    private Animal animal;

    @Enumerated(EnumType.STRING)
    @Column(name = "risk_level", nullable = false)
    private RiskLevel riskLevel;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String recommendation;

    @Column(nullable = false)
    private boolean active = true;

    public Recommendation() {}

    private Recommendation(Builder builder) {
        this.id = builder.id;
        this.animal = builder.animal;
        this.riskLevel = builder.riskLevel;
        this.recommendation = builder.recommendation;
        this.active = builder.active;
    }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private Long id;
        private Animal animal;
        private RiskLevel riskLevel;
        private String recommendation;
        private boolean active = true;

        public Builder id(Long id)                    { this.id = id; return this; }
        public Builder animal(Animal a)               { this.animal = a; return this; }
        public Builder riskLevel(RiskLevel r)         { this.riskLevel = r; return this; }
        public Builder recommendation(String r)       { this.recommendation = r; return this; }
        public Builder active(boolean active)         { this.active = active; return this; }
        public Recommendation build()                 { return new Recommendation(this); }
    }

    public Long getId()                              { return id; }
    public void setId(Long id)                       { this.id = id; }
    public Animal getAnimal()                        { return animal; }
    public void setAnimal(Animal a)                  { this.animal = a; }
    public RiskLevel getRiskLevel()                  { return riskLevel; }
    public void setRiskLevel(RiskLevel r)            { this.riskLevel = r; }
    public String getRecommendation()                { return recommendation; }
    public void setRecommendation(String r)          { this.recommendation = r; }
    public boolean isActive()                        { return active; }
    public void setActive(boolean active)            { this.active = active; }
}

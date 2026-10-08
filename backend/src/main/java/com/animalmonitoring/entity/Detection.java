package com.animalmonitoring.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * A single detection event produced by the AI model.
 */
@Entity
@Table(name = "detections")
public class Detection {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "animal_id", nullable = false)
    private Animal animal;

    @Column(nullable = false)
    private Double confidence;

    @Column(nullable = false)
    private LocalDateTime timestamp;

    private String location;

    @Column(name = "intrusion_detected", nullable = false)
    private boolean intrusionDetected = false;

    @Enumerated(EnumType.STRING)
    @Column(name = "risk_level", nullable = false)
    private RiskLevel riskLevel;

    @Column(name = "image_path")
    private String imagePath;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "zone_id")
    private RestrictedZone zone;

    @OneToMany(mappedBy = "detection", fetch = FetchType.LAZY)
    private List<Alert> alerts;

    public Detection() {}

    private Detection(Builder builder) {
        this.id = builder.id;
        this.animal = builder.animal;
        this.confidence = builder.confidence;
        this.timestamp = builder.timestamp;
        this.location = builder.location;
        this.intrusionDetected = builder.intrusionDetected;
        this.riskLevel = builder.riskLevel;
        this.imagePath = builder.imagePath;
        this.zone = builder.zone;
    }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private Long id;
        private Animal animal;
        private Double confidence;
        private LocalDateTime timestamp;
        private String location;
        private boolean intrusionDetected = false;
        private RiskLevel riskLevel;
        private String imagePath;
        private RestrictedZone zone;

        public Builder id(Long id)                      { this.id = id; return this; }
        public Builder animal(Animal a)                 { this.animal = a; return this; }
        public Builder confidence(Double c)             { this.confidence = c; return this; }
        public Builder timestamp(LocalDateTime t)       { this.timestamp = t; return this; }
        public Builder location(String l)               { this.location = l; return this; }
        public Builder intrusionDetected(boolean b)     { this.intrusionDetected = b; return this; }
        public Builder riskLevel(RiskLevel r)           { this.riskLevel = r; return this; }
        public Builder imagePath(String i)              { this.imagePath = i; return this; }
        public Builder zone(RestrictedZone z)           { this.zone = z; return this; }
        public Detection build()                        { return new Detection(this); }
    }

    public Long getId()                              { return id; }
    public void setId(Long id)                       { this.id = id; }
    public Animal getAnimal()                        { return animal; }
    public void setAnimal(Animal a)                  { this.animal = a; }
    public Double getConfidence()                    { return confidence; }
    public void setConfidence(Double c)              { this.confidence = c; }
    public LocalDateTime getTimestamp()              { return timestamp; }
    public void setTimestamp(LocalDateTime t)        { this.timestamp = t; }
    public String getLocation()                      { return location; }
    public void setLocation(String l)                { this.location = l; }
    public boolean isIntrusionDetected()             { return intrusionDetected; }
    public void setIntrusionDetected(boolean b)      { this.intrusionDetected = b; }
    public RiskLevel getRiskLevel()                  { return riskLevel; }
    public void setRiskLevel(RiskLevel r)            { this.riskLevel = r; }
    public String getImagePath()                     { return imagePath; }
    public void setImagePath(String i)               { this.imagePath = i; }
    public RestrictedZone getZone()                  { return zone; }
    public void setZone(RestrictedZone z)            { this.zone = z; }
    public List<Alert> getAlerts()                   { return alerts; }
    public void setAlerts(List<Alert> a)             { this.alerts = a; }
}

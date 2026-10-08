package com.animalmonitoring.entity;

import jakarta.persistence.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.List;

/**
 * A geographic zone that must not be entered by specific animals.
 */
@Entity
@Table(name = "restricted_zones")
@EntityListeners(AuditingEntityListener.class)
public class RestrictedZone {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    private String location;

    @Column(name = "boundary_data", columnDefinition = "TEXT")
    private String boundaryData;

    @Column(nullable = false)
    private boolean active = true;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "zone", fetch = FetchType.LAZY)
    private List<Detection> detections;

    public RestrictedZone() {}

    private RestrictedZone(Builder builder) {
        this.id = builder.id;
        this.name = builder.name;
        this.description = builder.description;
        this.location = builder.location;
        this.boundaryData = builder.boundaryData;
        this.active = builder.active;
        this.createdAt = builder.createdAt;
    }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private Long id;
        private String name;
        private String description;
        private String location;
        private String boundaryData;
        private boolean active = true;
        private LocalDateTime createdAt;

        public Builder id(Long id)                    { this.id = id; return this; }
        public Builder name(String name)              { this.name = name; return this; }
        public Builder description(String d)          { this.description = d; return this; }
        public Builder location(String l)             { this.location = l; return this; }
        public Builder boundaryData(String b)         { this.boundaryData = b; return this; }
        public Builder active(boolean active)         { this.active = active; return this; }
        public Builder createdAt(LocalDateTime t)     { this.createdAt = t; return this; }
        public RestrictedZone build()                 { return new RestrictedZone(this); }
    }

    public Long getId()                              { return id; }
    public void setId(Long id)                       { this.id = id; }
    public String getName()                          { return name; }
    public void setName(String name)                 { this.name = name; }
    public String getDescription()                   { return description; }
    public void setDescription(String d)             { this.description = d; }
    public String getLocation()                      { return location; }
    public void setLocation(String l)                { this.location = l; }
    public String getBoundaryData()                  { return boundaryData; }
    public void setBoundaryData(String b)            { this.boundaryData = b; }
    public boolean isActive()                        { return active; }
    public void setActive(boolean active)            { this.active = active; }
    public LocalDateTime getCreatedAt()              { return createdAt; }
    public void setCreatedAt(LocalDateTime t)        { this.createdAt = t; }
    public List<Detection> getDetections()           { return detections; }
    public void setDetections(List<Detection> d)     { this.detections = d; }
}

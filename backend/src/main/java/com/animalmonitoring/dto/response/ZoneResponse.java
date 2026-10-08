package com.animalmonitoring.dto.response;

import java.time.LocalDateTime;

public class ZoneResponse {
    private Long id;
    private String name, description, location, boundaryData;
    private boolean active;
    private LocalDateTime createdAt;

    public ZoneResponse() {}

    private ZoneResponse(Builder b) {
        this.id = b.id; this.name = b.name; this.description = b.description;
        this.location = b.location; this.boundaryData = b.boundaryData;
        this.active = b.active; this.createdAt = b.createdAt;
    }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private Long id; private String name, description, location, boundaryData;
        private boolean active; private LocalDateTime createdAt;
        public Builder id(Long v)                  { id = v; return this; }
        public Builder name(String v)              { name = v; return this; }
        public Builder description(String v)       { description = v; return this; }
        public Builder location(String v)          { location = v; return this; }
        public Builder boundaryData(String v)      { boundaryData = v; return this; }
        public Builder active(boolean v)           { active = v; return this; }
        public Builder createdAt(LocalDateTime v)  { createdAt = v; return this; }
        public ZoneResponse build()                { return new ZoneResponse(this); }
    }

    public Long getId()               { return id; }
    public String getName()           { return name; }
    public String getDescription()    { return description; }
    public String getLocation()       { return location; }
    public String getBoundaryData()   { return boundaryData; }
    public boolean isActive()         { return active; }
    public LocalDateTime getCreatedAt(){ return createdAt; }
}

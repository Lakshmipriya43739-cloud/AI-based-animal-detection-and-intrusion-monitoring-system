package com.animalmonitoring.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class ZoneRequest {

    @NotBlank(message = "Zone name is required")
    @Size(max = 150)
    private String name;

    private String description;
    private String location;
    private String boundaryData;
    private boolean active = true;

    public ZoneRequest() {}

    public String getName()                    { return name; }
    public void setName(String name)           { this.name = name; }
    public String getDescription()             { return description; }
    public void setDescription(String d)       { this.description = d; }
    public String getLocation()                { return location; }
    public void setLocation(String l)          { this.location = l; }
    public String getBoundaryData()            { return boundaryData; }
    public void setBoundaryData(String b)      { this.boundaryData = b; }
    public boolean isActive()                  { return active; }
    public void setActive(boolean active)      { this.active = active; }
}

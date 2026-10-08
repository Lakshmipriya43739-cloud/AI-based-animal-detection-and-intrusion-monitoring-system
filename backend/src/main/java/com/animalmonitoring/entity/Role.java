package com.animalmonitoring.entity;

/**
 * Roles available in the system.
 * ADMIN  - full access, manages users/animals/zones/recommendations
 * FARMER - views detections, alerts, recommendations for their area
 * OFFICER - wildlife/forest officer, views high-risk detections and alerts
 */
public enum Role {
    ADMIN,
    FARMER,
    OFFICER
}

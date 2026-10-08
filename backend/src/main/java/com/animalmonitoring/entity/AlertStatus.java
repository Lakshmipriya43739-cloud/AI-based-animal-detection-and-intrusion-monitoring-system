package com.animalmonitoring.entity;

/**
 * Lifecycle status of an alert.
 * PENDING     - created but not yet dispatched
 * SENT        - successfully dispatched to recipient
 * DELIVERED   - confirmed delivery (where delivery receipts are supported)
 * FAILED      - dispatch attempt failed
 * ACKNOWLEDGED - recipient has acknowledged the alert
 */
public enum AlertStatus {
    PENDING,
    SENT,
    DELIVERED,
    FAILED,
    ACKNOWLEDGED
}

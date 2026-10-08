package com.animalmonitoring.entity;

/**
 * Type of alert sent to users.
 * EMAIL - sent via email
 * SMS   - sent via SMS
 * PUSH  - in-app push notification
 * ALL   - all channels simultaneously
 */
public enum AlertType {
    EMAIL,
    SMS,
    PUSH,
    ALL
}

package com.animalmonitoring.dto.response;

import com.animalmonitoring.entity.AlertStatus;
import com.animalmonitoring.entity.AlertType;
import java.time.LocalDateTime;

public class AlertResponse {
    private Long id, detectionId, recipientId;
    private String recipientName, message;
    private AlertType alertType;
    private AlertStatus status;
    private LocalDateTime sentAt;

    public AlertResponse() {}

    private AlertResponse(Builder b) {
        this.id = b.id; this.detectionId = b.detectionId; this.recipientId = b.recipientId;
        this.recipientName = b.recipientName; this.alertType = b.alertType; this.status = b.status;
        this.message = b.message; this.sentAt = b.sentAt;
    }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private Long id, detectionId, recipientId; private String recipientName, message;
        private AlertType alertType; private AlertStatus status; private LocalDateTime sentAt;
        public Builder id(Long v)                { id = v; return this; }
        public Builder detectionId(Long v)       { detectionId = v; return this; }
        public Builder recipientId(Long v)       { recipientId = v; return this; }
        public Builder recipientName(String v)   { recipientName = v; return this; }
        public Builder alertType(AlertType v)    { alertType = v; return this; }
        public Builder status(AlertStatus v)     { status = v; return this; }
        public Builder message(String v)         { message = v; return this; }
        public Builder sentAt(LocalDateTime v)   { sentAt = v; return this; }
        public AlertResponse build()             { return new AlertResponse(this); }
    }

    public Long getId()                { return id; }
    public Long getDetectionId()       { return detectionId; }
    public Long getRecipientId()       { return recipientId; }
    public String getRecipientName()   { return recipientName; }
    public AlertType getAlertType()    { return alertType; }
    public AlertStatus getStatus()     { return status; }
    public String getMessage()         { return message; }
    public LocalDateTime getSentAt()   { return sentAt; }
}

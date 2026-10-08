package com.animalmonitoring.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

/**
 * An alert dispatched to a user in response to a detection event.
 */
@Entity
@Table(name = "alerts")
public class Alert {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "detection_id", nullable = false)
    private Detection detection;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "recipient_id", nullable = false)
    private User recipient;

    @Enumerated(EnumType.STRING)
    @Column(name = "alert_type", nullable = false)
    private AlertType alertType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AlertStatus status = AlertStatus.PENDING;

    @Column(columnDefinition = "TEXT")
    private String message;

    @Column(name = "sent_at")
    private LocalDateTime sentAt;

    public Alert() {}

    private Alert(Builder builder) {
        this.id = builder.id;
        this.detection = builder.detection;
        this.recipient = builder.recipient;
        this.alertType = builder.alertType;
        this.status = builder.status != null ? builder.status : AlertStatus.PENDING;
        this.message = builder.message;
        this.sentAt = builder.sentAt;
    }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private Long id;
        private Detection detection;
        private User recipient;
        private AlertType alertType;
        private AlertStatus status = AlertStatus.PENDING;
        private String message;
        private LocalDateTime sentAt;

        public Builder id(Long id)                    { this.id = id; return this; }
        public Builder detection(Detection d)         { this.detection = d; return this; }
        public Builder recipient(User r)              { this.recipient = r; return this; }
        public Builder alertType(AlertType t)         { this.alertType = t; return this; }
        public Builder status(AlertStatus s)          { this.status = s; return this; }
        public Builder message(String m)              { this.message = m; return this; }
        public Builder sentAt(LocalDateTime t)        { this.sentAt = t; return this; }
        public Alert build()                          { return new Alert(this); }
    }

    public Long getId()                              { return id; }
    public void setId(Long id)                       { this.id = id; }
    public Detection getDetection()                  { return detection; }
    public void setDetection(Detection d)            { this.detection = d; }
    public User getRecipient()                       { return recipient; }
    public void setRecipient(User r)                 { this.recipient = r; }
    public AlertType getAlertType()                  { return alertType; }
    public void setAlertType(AlertType t)            { this.alertType = t; }
    public AlertStatus getStatus()                   { return status; }
    public void setStatus(AlertStatus s)             { this.status = s; }
    public String getMessage()                       { return message; }
    public void setMessage(String m)                 { this.message = m; }
    public LocalDateTime getSentAt()                 { return sentAt; }
    public void setSentAt(LocalDateTime t)           { this.sentAt = t; }
}

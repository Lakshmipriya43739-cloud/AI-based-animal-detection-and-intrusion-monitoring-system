package com.animalmonitoring.entity;

import jakarta.persistence.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Represents a system user — an ADMIN, FARMER, or OFFICER.
 */
@Entity
@Table(name = "users",
        uniqueConstraints = @UniqueConstraint(name = "uc_users_email", columnNames = "email"))
@EntityListeners(AuditingEntityListener.class)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    private String phone;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    private String location;

    @Column(nullable = false)
    private boolean enabled = true;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "recipient", fetch = FetchType.LAZY)
    private List<Alert> alerts;

    public User() {}

    private User(Builder builder) {
        this.id = builder.id;
        this.name = builder.name;
        this.email = builder.email;
        this.password = builder.password;
        this.phone = builder.phone;
        this.role = builder.role;
        this.location = builder.location;
        this.enabled = builder.enabled;
        this.createdAt = builder.createdAt;
    }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private Long id;
        private String name;
        private String email;
        private String password;
        private String phone;
        private Role role;
        private String location;
        private boolean enabled = true;
        private LocalDateTime createdAt;

        public Builder id(Long id)               { this.id = id; return this; }
        public Builder name(String name)          { this.name = name; return this; }
        public Builder email(String email)        { this.email = email; return this; }
        public Builder password(String password)  { this.password = password; return this; }
        public Builder phone(String phone)        { this.phone = phone; return this; }
        public Builder role(Role role)            { this.role = role; return this; }
        public Builder location(String location)  { this.location = location; return this; }
        public Builder enabled(boolean enabled)   { this.enabled = enabled; return this; }
        public Builder createdAt(LocalDateTime t) { this.createdAt = t; return this; }
        public User build()                       { return new User(this); }
    }

    public Long getId()                { return id; }
    public void setId(Long id)         { this.id = id; }
    public String getName()            { return name; }
    public void setName(String name)   { this.name = name; }
    public String getEmail()           { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPassword()                  { return password; }
    public void setPassword(String password)     { this.password = password; }
    public String getPhone()                     { return phone; }
    public void setPhone(String phone)           { this.phone = phone; }
    public Role getRole()                        { return role; }
    public void setRole(Role role)               { this.role = role; }
    public String getLocation()                  { return location; }
    public void setLocation(String location)     { this.location = location; }
    public boolean isEnabled()                   { return enabled; }
    public void setEnabled(boolean enabled)      { this.enabled = enabled; }
    public LocalDateTime getCreatedAt()          { return createdAt; }
    public void setCreatedAt(LocalDateTime t)    { this.createdAt = t; }
    public List<Alert> getAlerts()               { return alerts; }
    public void setAlerts(List<Alert> alerts)    { this.alerts = alerts; }
}

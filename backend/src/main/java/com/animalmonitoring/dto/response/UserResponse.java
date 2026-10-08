package com.animalmonitoring.dto.response;

import com.animalmonitoring.entity.Role;
import java.time.LocalDateTime;

public class UserResponse {
    private Long id;
    private String name, email, phone, location;
    private Role role;
    private boolean enabled;
    private LocalDateTime createdAt;

    public UserResponse() {}

    private UserResponse(Builder b) {
        this.id = b.id; this.name = b.name; this.email = b.email; this.phone = b.phone;
        this.role = b.role; this.location = b.location; this.enabled = b.enabled; this.createdAt = b.createdAt;
    }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private Long id; private String name, email, phone, location; private Role role;
        private boolean enabled; private LocalDateTime createdAt;
        public Builder id(Long v)               { id = v; return this; }
        public Builder name(String v)           { name = v; return this; }
        public Builder email(String v)          { email = v; return this; }
        public Builder phone(String v)          { phone = v; return this; }
        public Builder role(Role v)             { role = v; return this; }
        public Builder location(String v)       { location = v; return this; }
        public Builder enabled(boolean v)       { enabled = v; return this; }
        public Builder createdAt(LocalDateTime v){ createdAt = v; return this; }
        public UserResponse build()             { return new UserResponse(this); }
    }

    public Long getId()                { return id; }
    public String getName()            { return name; }
    public String getEmail()           { return email; }
    public String getPhone()           { return phone; }
    public Role getRole()              { return role; }
    public String getLocation()        { return location; }
    public boolean isEnabled()         { return enabled; }
    public LocalDateTime getCreatedAt(){ return createdAt; }
}

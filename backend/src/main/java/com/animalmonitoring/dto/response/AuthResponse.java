package com.animalmonitoring.dto.response;

import com.animalmonitoring.entity.Role;

public class AuthResponse {
    private String token;
    private String tokenType;
    private Long userId;
    private String name;
    private String email;
    private Role role;
    private long expiresIn;

    public AuthResponse() {}

    private AuthResponse(Builder b) {
        this.token = b.token; this.tokenType = b.tokenType; this.userId = b.userId;
        this.name = b.name; this.email = b.email; this.role = b.role; this.expiresIn = b.expiresIn;
    }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private String token, tokenType, name, email;
        private Long userId; private Role role; private long expiresIn;
        public Builder token(String v)      { token = v; return this; }
        public Builder tokenType(String v)  { tokenType = v; return this; }
        public Builder userId(Long v)       { userId = v; return this; }
        public Builder name(String v)       { name = v; return this; }
        public Builder email(String v)      { email = v; return this; }
        public Builder role(Role v)         { role = v; return this; }
        public Builder expiresIn(long v)    { expiresIn = v; return this; }
        public AuthResponse build()         { return new AuthResponse(this); }
    }

    public String getToken()      { return token; }
    public String getTokenType()  { return tokenType; }
    public Long getUserId()       { return userId; }
    public String getName()       { return name; }
    public String getEmail()      { return email; }
    public Role getRole()         { return role; }
    public long getExpiresIn()    { return expiresIn; }
}

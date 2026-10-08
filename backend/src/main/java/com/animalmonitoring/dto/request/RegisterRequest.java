package com.animalmonitoring.dto.request;

import com.animalmonitoring.entity.Role;
import jakarta.validation.constraints.*;

public class RegisterRequest {

    @NotBlank(message = "Name is required")
    @Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
    private String name;

    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 8, max = 100, message = "Password must be at least 8 characters")
    @Pattern(
        regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).*$",
        message = "Password must contain at least one uppercase letter, one lowercase letter, and one digit"
    )
    private String password;

    @Pattern(regexp = "^[+]?[0-9]{7,15}$", message = "Phone number is invalid")
    private String phone;

    @NotNull(message = "Role is required")
    private Role role;

    private String location;

    public RegisterRequest() {}

    public String getName()             { return name; }
    public void setName(String name)    { this.name = name; }
    public String getEmail()            { return email; }
    public void setEmail(String email)  { this.email = email; }
    public String getPassword()                  { return password; }
    public void setPassword(String password)     { this.password = password; }
    public String getPhone()                     { return phone; }
    public void setPhone(String phone)           { this.phone = phone; }
    public Role getRole()                        { return role; }
    public void setRole(Role role)               { this.role = role; }
    public String getLocation()                  { return location; }
    public void setLocation(String location)     { this.location = location; }
}

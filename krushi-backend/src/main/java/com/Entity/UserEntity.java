package com.Entity;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "users")
public class UserEntity {
    @Id
    @GeneratedValue
    private UUID id;

    @Column(unique = true, nullable = false)
    private String username; // could be phone or email

    @Column(nullable = false)
    private String passwordHash;

    @Column(nullable = false)
    private String role = "farmer";

    @Column
    private String preferredLanguage = "en";

    @Column
    private Instant createdAt = Instant.now();

    public UserEntity() {}
    public UserEntity(String username, String passwordHash){
        this.username = username;
        this.passwordHash = passwordHash;
    }

    // getters/setters...
    public UUID getId(){return id;}
    public String getUsername(){return username;}
    public void setUsername(String u){this.username = u;}
    public String getPasswordHash(){return passwordHash;}
    public void setPasswordHash(String p){this.passwordHash = p;}
    public String getRole(){return role;}
    public String getPreferredLanguage(){return preferredLanguage;}
    public void setPreferredLanguage(String l){this.preferredLanguage = l;}
}

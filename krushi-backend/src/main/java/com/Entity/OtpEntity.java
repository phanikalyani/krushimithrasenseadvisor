package com.krushi.entity;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "otps")
public class OtpEntity {
    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable=false)
    private String username;

    @Column(nullable=false)
    private String code;

    @Column(nullable=false)
    private Instant expiresAt;

    @Column(nullable=false)
    private Instant createdAt = Instant.now();

    // getters/setters...
    public OtpEntity() {}
    public OtpEntity(String username, String code, Instant expiresAt) {
        this.username = username; this.code = code; this.expiresAt = expiresAt;
    }

    public UUID getId(){ return id; }
    public String getUsername(){ return username; }
    public String getCode(){ return code; }
    public Instant getExpiresAt(){ return expiresAt; }
}

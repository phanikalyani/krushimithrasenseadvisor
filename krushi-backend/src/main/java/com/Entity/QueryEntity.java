package com.Entity;



import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "queries")
public class QueryEntity {
    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable=false)
    private String text;

    @Column
    private String response;

    @Column(nullable=false)
    private Instant createdAt = Instant.now();

    public QueryEntity() {}
    public QueryEntity(String text){ this.text = text; }

    // getters / setters
    public UUID getId(){return id;}
    public String getText(){return text;}
    public void setText(String t){this.text = t;}
    public String getResponse(){return response;}
    public void setResponse(String r){this.response = r;}
    public Instant getCreatedAt(){return createdAt;}
}

package com.dev.urlshortener.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "url_mappings")
public class UrlMapping {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 2048)
    private String longUrl;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    public UrlMapping() {}

    public UrlMapping(String longUrl) {
        this.longUrl = longUrl;
    }

    public Long getId() { return id; }
    public String getLongUrl() { return longUrl; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
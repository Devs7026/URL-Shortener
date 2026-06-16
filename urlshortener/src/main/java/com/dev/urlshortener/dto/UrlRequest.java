package com.dev.urlshortener.dto;

import jakarta.validation.constraints.NotBlank;

public class UrlRequest {

    @NotBlank(message = "Long URL cannot be empty")
    private String longUrl;

    public UrlRequest() {
    }

    public UrlRequest(String longUrl) {
        this.longUrl = longUrl;
    }

    public String getLongUrl() {
        return longUrl;
    }

    public void setLongUrl(String longUrl) {
        this.longUrl = longUrl;
    }
}

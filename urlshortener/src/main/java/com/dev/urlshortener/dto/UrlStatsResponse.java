package com.dev.urlshortener.dto;

public class UrlStatsResponse {

    private String longUrl;
    private Long clickCount;

    public UrlStatsResponse() {
    }

    public UrlStatsResponse(String longUrl, Long clickCount) {
        this.longUrl = longUrl;
        this.clickCount = clickCount;
    }

    public String getLongUrl() {
        return longUrl;
    }

    public void setLongUrl(String longUrl) {
        this.longUrl = longUrl;
    }

    public Long getClickCount() {
        return clickCount;
    }

    public void setClickCount(Long clickCount) {
        this.clickCount = clickCount;
    }
}

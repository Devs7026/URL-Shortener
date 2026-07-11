package com.dev.urlshortener.controller;

import java.net.URI;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.dev.urlshortener.dto.UrlRequest;
import com.dev.urlshortener.dto.UrlResponse;
import com.dev.urlshortener.dto.UrlStatsResponse;
import com.dev.urlshortener.service.UrlService;

import jakarta.validation.Valid;

@RestController
public class UrlController {

    private final UrlService urlService;

    public UrlController(UrlService urlService) {
        this.urlService = urlService;
    }

    @PostMapping("/api/shorten")
    public ResponseEntity<UrlResponse> shortenUrl(@Valid @RequestBody UrlRequest request) {

        String shortCode = urlService.shortenUrl(request.getLongUrl(), request.getCustomAlias());

        String shortUrl = "http://localhost:8081/" + shortCode;

        return ResponseEntity.ok(new UrlResponse(shortUrl));
    }

    @GetMapping("/{shortCode}")
    public ResponseEntity<Void> redirectToLongUrl(
            @PathVariable String shortCode) {

        String longUrl = urlService.getLongUrl(shortCode);

        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(URI.create(longUrl));

        return ResponseEntity.status(302).headers(headers).build();
    }

    @GetMapping("/api/stats/{shortCode}")
    public ResponseEntity<UrlStatsResponse> getStatistics(
            @PathVariable String shortCode) {

        return ResponseEntity.ok(
                urlService.getStatistics(shortCode)
        );
    }
}

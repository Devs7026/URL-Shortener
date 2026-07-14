package com.dev.urlshortener.service;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dev.urlshortener.dto.UrlStatsResponse;
import com.dev.urlshortener.exception.AliasAlreadyExistsException;
import com.dev.urlshortener.exception.UrlExpiredException;
import com.dev.urlshortener.exception.UrlNotFoundException;
import com.dev.urlshortener.model.UrlMapping;
import com.dev.urlshortener.repository.UrlRepository;
import com.dev.urlshortener.util.Base62Converter;

@Service
public class UrlService {

    private final UrlRepository urlRepository;
    private final Base62Converter base62Converter;

    public UrlService(UrlRepository urlRepository,
            Base62Converter base62Converter) {
        this.urlRepository = urlRepository;
        this.base62Converter = base62Converter;
    }

    /**
     * Creates a short URL code from a long URL.
     *
     * Flow: - If a custom alias is provided: validate uniqueness, save with
     * alias, return alias. - If no custom alias: deduplicate by long URL, then
     * auto-generate a Base62 code.
     */
    public String shortenUrl(String longUrl, String customAlias, LocalDateTime expiresAt) {

        if (expiresAt != null && expiresAt.isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException(
                    "Expiration date must be in the future");
        }

        boolean hasAlias = customAlias != null && !customAlias.isBlank();

        if (hasAlias) {
            // Validate alias uniqueness
            if (urlRepository.findByCustomAlias(customAlias).isPresent()) {
                throw new AliasAlreadyExistsException("Custom alias '" + customAlias + "' already exists");
            }

            // Create a new mapping with the custom alias
            UrlMapping urlMapping = new UrlMapping(longUrl);
            urlMapping.setExpiresAt(expiresAt);
            urlMapping.setCustomAlias(customAlias);
            urlRepository.save(urlMapping);

            return customAlias;
        }

        // No alias — deduplicate by long URL
        Optional<UrlMapping> existingUrl = urlRepository.findByLongUrl(longUrl);
        if (existingUrl.isPresent()) {
            UrlMapping existing = existingUrl.get();
            // If the existing record itself has a custom alias, return that
            if (existing.getCustomAlias() != null && !existing.getCustomAlias().isBlank()) {
                return existing.getCustomAlias();
            }
            return base62Converter.encode(existing.getId());
        }

        UrlMapping urlMapping = new UrlMapping(longUrl);
        urlMapping.setExpiresAt(expiresAt);
        UrlMapping savedMapping = urlRepository.save(urlMapping);

        return base62Converter.encode(savedMapping.getId());
    }

    /**
     * Retrieves original URL from a short code or custom alias.
     *
     * Flow: 1. Try to resolve as a custom alias first. 2. Fall back to Base62
     * decoding.
     */
    @Transactional
    public String getLongUrl(String shortCode) {

        // 1. Try to resolve as a custom alias
        Optional<UrlMapping> byAlias = urlRepository.findByCustomAlias(shortCode);
        if (byAlias.isPresent()) {
            UrlMapping mapping = byAlias.get();

            if (mapping.getExpiresAt() != null
                    && mapping.getExpiresAt().isBefore(LocalDateTime.now())) {

                throw new UrlExpiredException(
                        "This short URL has expired.");
            }
            urlRepository.incrementClickCount(mapping.getId());
            return mapping.getLongUrl();
        }

        // 2. Fall back to Base62 decoding
        long id = base62Converter.decode(shortCode);

        UrlMapping mapping = urlRepository.findById(id)
                .orElseThrow(() -> new UrlNotFoundException("URL not found"));

        if (mapping.getExpiresAt() != null
                && mapping.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new UrlExpiredException("This short URL has expired.");
        }

        urlRepository.incrementClickCount(id);

        return mapping.getLongUrl();
    }

    public UrlStatsResponse getStatistics(String shortCode) {

        long id = base62Converter.decode(shortCode);

        UrlMapping urlMapping = urlRepository.findById(id)
                .orElseThrow(()
                        -> new UrlNotFoundException("URL not found for code: " + shortCode));

        return new UrlStatsResponse(
                urlMapping.getLongUrl(),
                urlMapping.getClickCount()
        );
    }
}

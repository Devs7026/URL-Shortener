package com.dev.urlshortener.service;

import org.springframework.stereotype.Service;

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
     * Flow: 1. Save URL in database. 2. Database generates ID. 3. Encode ID
     * using Base62. 4. Return encoded string.
     */
    public String shortenUrl(String longUrl) {

        UrlMapping urlMapping = new UrlMapping(longUrl);

        UrlMapping savedMapping = urlRepository.save(urlMapping);

        return base62Converter.encode(savedMapping.getId());
    }

    /**
     * Retrieves original URL from short code.
     *
     * Flow: 1. Decode Base62 string to ID. 2. Fetch record from database. 3.
     * Return original URL.
     */
    public String getLongUrl(String shortCode) {

        long id = base62Converter.decode(shortCode);

        UrlMapping urlMapping = urlRepository.findById(id)
                .orElseThrow(()
                        -> new UrlNotFoundException("URL not found for code: " + shortCode));

        return urlMapping.getLongUrl();
    }
}

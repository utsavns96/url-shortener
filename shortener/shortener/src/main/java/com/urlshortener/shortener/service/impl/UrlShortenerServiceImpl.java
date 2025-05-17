package com.urlshortener.shortener.service.impl;

import com.urlshortener.shortener.model.UrlMapping;
import com.urlshortener.shortener.repository.UrlMappingRepository;
import com.urlshortener.shortener.service.UrlShortenerService;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

public class UrlShortenerServiceImpl implements UrlShortenerService {

    private final UrlMappingRepository repository;

    public UrlShortenerServiceImpl(UrlMappingRepository repository) {
        this.repository = repository;
    }


    @Override
    public String createShortURL(String originalURL, Instant expiryTime) {
        // Logic to create a short URL and save it to the repository
        String shortURL = UUID.randomUUID().toString().substring(0, 8); // Simple short URL generation
        UrlMapping mapping = new UrlMapping(shortURL, originalURL, expiryTime.toEpochMilli());
        repository.save(mapping);
        return shortURL;
    }

    @Override
    public Optional<String> getOriginalURL(String shortURL) {
        // Logic to retrieve the original URL from the repository
        return repository.findByShortURL(shortURL)
                .map(UrlMapping::getOriginalURL);
    }
}

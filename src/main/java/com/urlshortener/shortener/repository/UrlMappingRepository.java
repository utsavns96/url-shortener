package com.urlshortener.shortener.repository;

import com.urlshortener.shortener.model.UrlMapping;

import java.util.Optional;

public interface UrlMappingRepository {
    void save(UrlMapping urlMapping);
    Optional<UrlMapping> findByShortURL(String shortURL);
    Optional<UrlMapping> findByOriginalUrlHash(String originalURL);
}

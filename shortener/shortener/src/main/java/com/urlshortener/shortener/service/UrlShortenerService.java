package com.urlshortener.shortener.service;

import java.time.Instant;
import java.util.Optional;

public interface UrlShortenerService {
    String createShortURL(String originalURL, Instant expiryTime);
    Optional<String> getOriginalURL(String shortURL);
}

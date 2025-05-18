package com.urlshortener.shortener.service;

import java.time.Instant;
import java.util.Optional;

public interface UrlShortenerService {
    String createShortUrl(String originalURL, Instant expiryTime);
    Optional<String> getOriginalUrl(String shortURL);
}

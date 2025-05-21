package com.urlshortener.shortener.service;

import java.util.Optional;

public interface UrlShortenerService {
    String createShortUrl(String originalURL);
    Optional<String> getOriginalUrl(String shortURL);
}

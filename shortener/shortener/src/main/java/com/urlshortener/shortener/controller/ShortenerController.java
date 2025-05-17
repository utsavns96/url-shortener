package com.urlshortener.shortener.controller;

import com.urlshortener.shortener.service.UrlShortenerService;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;

@RestController
@RequestMapping("/api")
public class ShortenerController {

    private final UrlShortenerService service;

    public ShortenerController(UrlShortenerService service) {
        this.service = service;
    }

    @GetMapping("/{shortURL}")
    public String redirectToOriginal(@PathVariable String shortURL) {
        // Logic to retrieve the original URL from the database using the short URL
        // and redirect the user to the original URL.
        return service.getOriginalURL(shortURL)
                .map(url -> "Redirecting to: " + url)
                .orElse("URL not found or expired");
    }

    @PostMapping("/shorten")
    public String createShortURL(@RequestBody String originalURL){
        // Logic to create a short URL from the original URL and store it in the database.
        // Return the generated short URL.
        return service.createShortURL(originalURL, Instant.now().plusSeconds(3600));
    }
}

package com.urlshortener.shortener.controller;

import com.urlshortener.shortener.service.UrlShortenerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api")
public class ShortenerController {

    private final UrlShortenerService service;

    public ShortenerController(UrlShortenerService service) {
        this.service = service;
    }

    @GetMapping("/{shortUrl}")
    public ResponseEntity<Object> redirectToOriginal(@PathVariable String shortUrl) {
        // Logic to retrieve the original URL from the database using the short URL
        // and redirect the user to the original URL.
        log.info("Redirect request for {}", shortUrl);
        String targetUrl = service.getOriginalUrl(shortUrl)
                .orElse(null);
        log.info("Redirecting to {}", targetUrl);
        if(targetUrl != null) {
//            return ResponseEntity.status(HttpStatus.FOUND)
//                    .header(HttpHeaders.LOCATION, targetUrl)
//                    .build();
            return ResponseEntity.ok("{\"message\":\"Redirecting to " + targetUrl + "\"}");

        }
        else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("URL not found");
        }
//        return service.getOriginalUrl(shortUrl)
//                .map(url -> ResponseEntity.status(HttpStatus.FOUND)
//                        .header(HttpHeaders.LOCATION, url)
//                        .build())
//                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/shorten")
    public String createShortURL(@RequestBody String originalUrl){
        // Logic to create a short URL from the original URL and store it in the database.
        // Return the generated short URL.
        log.info("Shorten request for {}", originalUrl);
        return service.createShortUrl(originalUrl);
    }
}

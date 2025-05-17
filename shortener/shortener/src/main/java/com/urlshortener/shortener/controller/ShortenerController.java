package com.urlshortener.shortener.controller;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class ShortenerController {


    public String redirectToOriginal(@PathVariable String shortURL) {
        // Logic to retrieve the original URL from the database using the short URL
        // and redirect the user to the original URL.
        return "Redirecting to original URL for: " + shortURL;
    }

    public String createShortURL(@RequestBody String originalURL){
        // Logic to create a short URL from the original URL and store it in the database.
        // Return the generated short URL.
        return "Generated short URL for: " + originalURL;
    }
}

package com.urlshortener.shortener.service.impl;

import com.urlshortener.shortener.model.UrlMapping;
import com.urlshortener.shortener.repository.UrlMappingRepository;
import com.urlshortener.shortener.service.UrlShortenerService;
import org.springframework.stereotype.Service;


import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.Base64;
import java.util.Optional;

@Service
public class UrlShortenerServiceImpl implements UrlShortenerService {

    private final UrlMappingRepository repository;

    public UrlShortenerServiceImpl(UrlMappingRepository repository) {
        this.repository = repository;
    }


    @Override
    public String createShortUrl(String originalUrl) {
        // Logic to create a short URL and save it to the repository

        String originalUrlHash = computeUrlHash(originalUrl);
        Instant now = Instant.now(); // Get the current time

        //1. Check if the original URL already exists
        Optional<UrlMapping> existingMapping = repository.findByOriginalUrlHash(originalUrlHash);
        if(existingMapping.isPresent()){
            UrlMapping mapping = existingMapping.get();
            if(mapping.getExpiryTime() < now.getEpochSecond())
            {
                mapping.setExpiryTime(now.getEpochSecond()+3600);
                repository.save(mapping);
            }
            return mapping.getShortUrl();
        }
        //2. Else, generate new short URL by checking for collisions
        String shortUrl=null;
        int counter = 0;
        final int MAX_RETRIES = 10; // Maximum number of retries to avoid infinite loop
        //generate shortUrl
        while(counter<MAX_RETRIES){
            String input = (counter == 0) ? originalUrl : originalUrl + counter;
            shortUrl = generateShortUrl(input);
            Optional<UrlMapping> collisionCheckMapping = repository.findByShortURL(shortUrl);
            if(collisionCheckMapping.isEmpty()){

                break;
            }
            counter++;
        }
        if (counter == MAX_RETRIES) {
            throw new RuntimeException("Failed to generate unique short URL after " + MAX_RETRIES + " attempts");
        }

        UrlMapping mapping = new UrlMapping(shortUrl, originalUrl, now.getEpochSecond()+3600, originalUrlHash);
        repository.save(mapping);
        return shortUrl;
    }

    private String generateShortUrl(String originalUrl){
        //Using a consistent hash function to generate a short URL
        try{
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(originalUrl.getBytes(StandardCharsets.UTF_8));
            return Base64.getUrlEncoder().withoutPadding().encodeToString(hash).substring(0, 8);
        }
        catch (NoSuchAlgorithmException e)
        {
            throw new RuntimeException("Error generating short URL", e);
        }
    }

    private String computeUrlHash(String originalUrl){
        try{
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(originalUrl.getBytes(StandardCharsets.UTF_8));
            return Base64.getUrlEncoder().withoutPadding().encodeToString(hash);
        }
        catch(NoSuchAlgorithmException e){
            throw new RuntimeException("Error computing URL hash", e);
        }
    }

    @Override
    public Optional<String> getOriginalUrl(String shortURL) {
        // Logic to retrieve the original URL from the repository
        return repository.findByShortURL(shortURL)
                .map(UrlMapping::getOriginalUrl);
    }
}

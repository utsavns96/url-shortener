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
    public String createShortUrl(String originalURL, Instant expiryTime) {
        // Logic to create a short URL and save it to the repository
        //String shortURL = UUID.randomUUID().toString().substring(0, 8); // Simple short URL generation
        String shortURL = generateShortUrl(originalURL);
        Optional<UrlMapping> existingMapping = repository.findByShortURL(shortURL);
        if(existingMapping.isPresent()){
            // If the short URL already exists, we can either return it or generate a new one
            UrlMapping mapping = existingMapping.get();
            if(mapping.getExpiryTime() < expiryTime.getEpochSecond())
            {
                long newExpiryTime = expiryTime.getEpochSecond()+3600;
                mapping.setExpiryTime(newExpiryTime);
                repository.save(mapping);
            }
            return shortURL;
        }
        UrlMapping mapping = new UrlMapping(shortURL, originalURL, expiryTime.getEpochSecond());
        repository.save(mapping);
        return shortURL;
    }

    private String generateShortUrl(String originalUrl){
        //Using a consistent hash function to generate a short URL
        try{
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] hash = md.digest(originalUrl.getBytes(StandardCharsets.UTF_8));
            return Base64.getUrlEncoder().withoutPadding().encodeToString(hash).substring(0, 8);
        }
        catch (NoSuchAlgorithmException e)
        {
            throw new RuntimeException("Error generating short URL", e);
        }
    }

    @Override
    public Optional<String> getOriginalUrl(String shortURL) {
        // Logic to retrieve the original URL from the repository
        return repository.findByShortURL(shortURL)
                .map(UrlMapping::getOriginalUrl);
    }
}

package com.urlshortener.shortener.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UrlMapping {
    private String shortUrl;
    private String originalUrl;
    private long expiryTime;
    private String originalUrlHash; //Need to add this field to work around possible hash collision in shortUrl
}
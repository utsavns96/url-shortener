package com.urlshortener.shortener.repository.impl;

import com.urlshortener.shortener.repository.UrlMappingRepository;
import com.urlshortener.shortener.model.UrlMapping;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.GetItemRequest;
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Repository
public class UrlMappingRepositoryImpl implements UrlMappingRepository {
    private final DynamoDbClient dynamoDbClient;
    private final String tableName = "url-mappings";

    public UrlMappingRepositoryImpl(DynamoDbClient dynamoDbClient) {
        this.dynamoDbClient = dynamoDbClient;
    }

    @Override
    public void save(UrlMapping urlMapping) {
        // Implementation to save the URL mapping to DynamoDB
        // Use the DynamoDbClient to put the item in the table
        Map<String, AttributeValue> item = new HashMap<>();
        item.put("shortURL",AttributeValue.fromS(urlMapping.getShortURL()));
        item.put("originalURL", AttributeValue.fromS(urlMapping.getOriginalURL()));
        item.put("expiryTime", AttributeValue.fromN(String.valueOf(urlMapping.getExpiryTime())));

        PutItemRequest request = PutItemRequest.builder()
                .tableName(tableName)
                .item(item)
                .build();

        dynamoDbClient.putItem(request);
    }

    @Override
    public Optional<UrlMapping> findByShortURL(String shortURL){
        Map<String, AttributeValue> key = new HashMap<>();
        key.put("shortURL", AttributeValue.fromS(shortURL));

        GetItemRequest request = GetItemRequest.builder()
                .tableName(tableName)
                .key(key)
                .build();

        Map<String, AttributeValue> item = dynamoDbClient.getItem(request).item();
        if(item == null || item.isEmpty()) {
            return Optional.empty();
        }

        UrlMapping urlMapping = new UrlMapping(
                item.get("shortURL").s(),
                item.get("originalURL").s(),
                Long.parseLong(item.get("expiryTime").n())
        );
        return Optional.of(urlMapping);
    }

}

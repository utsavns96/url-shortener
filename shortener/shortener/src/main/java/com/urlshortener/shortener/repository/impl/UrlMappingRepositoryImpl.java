package com.urlshortener.shortener.repository.impl;

import com.urlshortener.shortener.repository.UrlMappingRepository;
import com.urlshortener.shortener.model.UrlMapping;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Repository
public class UrlMappingRepositoryImpl implements UrlMappingRepository {
    private final DynamoDbClient dynamoDbClient;
    private final String tableName = "urlmappings";
    private final String GSI_NAME = "originalUrlHash-index";
    public UrlMappingRepositoryImpl(DynamoDbClient dynamoDbClient) {
        this.dynamoDbClient = dynamoDbClient;
    }

    @Override
    public void save(UrlMapping urlMapping) {
        // Implementation to save the URL mapping to DynamoDB
        // Use the DynamoDbClient to put the item in the table
        Map<String, AttributeValue> item = new HashMap<>();
        item.put("shortUrl",AttributeValue.fromS(urlMapping.getShortUrl()));
        item.put("originalUrl", AttributeValue.fromS(urlMapping.getOriginalUrl()));
        item.put("expiryTime", AttributeValue.fromN(String.valueOf(urlMapping.getExpiryTime())));
        item.put("originalUrlHash", AttributeValue.fromS(urlMapping.getOriginalUrlHash()));

        PutItemRequest request = PutItemRequest.builder()
                .tableName(tableName)
                .item(item)
                .build();

        dynamoDbClient.putItem(request);
    }

    @Override
    public Optional<UrlMapping> findByShortURL(String shortURL){
        Map<String, AttributeValue> key = new HashMap<>();
        key.put("shortUrl", AttributeValue.fromS(shortURL));

        GetItemRequest request = GetItemRequest.builder()
                .tableName(tableName)
                .key(key)
                .build();

        Map<String, AttributeValue> item = dynamoDbClient.getItem(request).item();
        if(item == null || item.isEmpty()) {
            return Optional.empty();
        }

        UrlMapping urlMapping = new UrlMapping(
                item.get("shortUrl").s(),
                item.get("originalUrl").s(),
                Long.parseLong(item.get("expiryTime").n()),
                item.get("originalUrlHash").s()
        );
        return Optional.of(urlMapping);
    }

    @Override
    public Optional<UrlMapping> findByOriginalUrlHash(String originalUrlHash) {
        Map<String, AttributeValue> expressionValues = new HashMap<>();
        expressionValues.put(":originalUrlHash", AttributeValue.fromS(originalUrlHash));

        QueryRequest request = QueryRequest.builder()
                .tableName(tableName)
                .indexName(GSI_NAME)
                .keyConditionExpression("originalUrlHash = :originalUrlHash")
                .expressionAttributeValues(expressionValues)
                .build();

        QueryResponse response = dynamoDbClient.query(request);

        if(response == null || response.items().isEmpty()) {
            return Optional.empty();
        }

        Map<String, AttributeValue> item = response.items().get(0);

        UrlMapping urlMapping = new UrlMapping(
                item.get("shortUrl").s(),
                item.get("originalUrl").s(),
                Long.parseLong(item.get("expiryTime").n()),
                item.get("originalUrlHash").s()
        );
        return Optional.of(urlMapping);
    }

}

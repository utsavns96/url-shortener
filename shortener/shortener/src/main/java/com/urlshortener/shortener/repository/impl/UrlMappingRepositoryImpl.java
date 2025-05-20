package com.urlshortener.shortener.repository.impl;

import com.urlshortener.shortener.repository.UrlMappingRepository;
import com.urlshortener.shortener.model.UrlMapping;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Slf4j
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
        log.info("save - Saving URL mapping to DynamoDB");
        Map<String, AttributeValue> item = new HashMap<>();
        item.put("shortUrl",AttributeValue.fromS(urlMapping.getShortUrl()));
        item.put("originalUrl", AttributeValue.fromS(urlMapping.getOriginalUrl()));
        item.put("expiryTime", AttributeValue.fromN(String.valueOf(urlMapping.getExpiryTime())));
        item.put("originalUrlHash", AttributeValue.fromS(urlMapping.getOriginalUrlHash()));
        log.debug("save - Trying to save item: {}", item);
        PutItemRequest request = PutItemRequest.builder()
                .tableName(tableName)
                .item(item)
                .build();
        dynamoDbClient.putItem(request);
        log.debug("save - Item saved successfully");
    }

    @Override
    public Optional<UrlMapping> findByShortURL(String shortURL){
        log.info("findByShortURL - Looking up short URL: {}", shortURL);
        Map<String, AttributeValue> key = new HashMap<>();
        key.put("shortUrl", AttributeValue.fromS(shortURL));
        log.debug("findByShortURL - performing GET request to database");
        GetItemRequest request = GetItemRequest.builder()
                .tableName(tableName)
                .key(key)
                .build();

        Map<String, AttributeValue> item = dynamoDbClient.getItem(request).item();
        if(item == null || item.isEmpty()) {
            log.debug("findByShortURL - No item found");
            return Optional.empty();
        }
        log.debug("findByShortURL - unpacking item");
        UrlMapping urlMapping = new UrlMapping(
                item.get("shortUrl").s(),
                item.get("originalUrl").s(),
                Long.parseLong(item.get("expiryTime").n()),
                item.get("originalUrlHash").s()
        );
        log.info("findByShortURL - returning item");
        return Optional.of(urlMapping);
    }

    @Override
    public Optional<UrlMapping> findByOriginalUrlHash(String originalUrlHash) {
        log.info("findByOriginalUrlHash - Looking up originalUrlHash: {}", originalUrlHash);
        Map<String, AttributeValue> expressionValues = new HashMap<>();
        expressionValues.put(":originalUrlHash", AttributeValue.fromS(originalUrlHash));
        log.debug("findByOriginalUrlHash - performing query request");
        QueryRequest request = QueryRequest.builder()
                .tableName(tableName)
                .indexName(GSI_NAME)
                .keyConditionExpression("originalUrlHash = :originalUrlHash")
                .expressionAttributeValues(expressionValues)
                .build();

        QueryResponse response = dynamoDbClient.query(request);

        if(response == null || response.items().isEmpty()) {
            log.debug("findByOriginalUrlHash - No item found");
            return Optional.empty();
        }

        Map<String, AttributeValue> item = response.items().get(0);
        log.debug("findByOriginalUrlHash - unpacking item");
        UrlMapping urlMapping = new UrlMapping(
                item.get("shortUrl").s(),
                item.get("originalUrl").s(),
                Long.parseLong(item.get("expiryTime").n()),
                item.get("originalUrlHash").s()
        );
        log.info("findByOriginalUrlHash - returning item");
        return Optional.of(urlMapping);
    }

}

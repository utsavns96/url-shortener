package com.urlshortener.shortener.config;

import jakarta.annotation.PostConstruct;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.PropertiesPropertySource;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;

import java.io.InputStream;
import java.util.Properties;

@Component
public class S3ConfigLoader {
    private final String bucketName = "utsav-url-shortener";
    private final String key = "app-config/application.properties";

    private final S3Client s3Client;
    private final ConfigurableEnvironment environment;

    public S3ConfigLoader(ConfigurableEnvironment environment, S3Client s3Client)
    {
        this.environment = environment;
        this.s3Client = s3Client;
    }

    @PostConstruct
    public void init() {
        loadPropertiesFromS3();
    }

    public void loadPropertiesFromS3()
    {
        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build();

        try (InputStream inputStream = s3Client.getObject(getObjectRequest)){
            Properties properties = new Properties();
            properties.load(inputStream);
            PropertiesPropertySource S3PropertySource = new PropertiesPropertySource("S3Properties", properties);
            environment.getPropertySources().addFirst(S3PropertySource);
        }
        catch (Exception e)
        {
            throw new RuntimeException("Failed to load properties from S3", e);
        }
    }
}

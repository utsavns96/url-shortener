package com.urlshortener.shortener.config;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.PropertiesPropertySource;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;

import java.io.InputStream;
import java.util.Properties;

@Slf4j
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
        log.debug("S3ConfigLoader - Loading properties from S3 bucket: {} with key: {}", bucketName, key);
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
            //Fallback to local application.properties
            log.debug("S3ConfigLoader - Failed to load properties from S3, falling back to local application.properties", e);
            try(InputStream localInput = getClass().getClassLoader().getResourceAsStream("application.properties")) {
                if (localInput != null) {
                    Properties properties = new Properties();
                    properties.load(localInput);
                    PropertiesPropertySource localPropertySource = new PropertiesPropertySource("LocalProperties", properties);
                    environment.getPropertySources().addFirst(localPropertySource);
                }
            } catch (Exception ex) {
                log.error("S3ConfigLoader - Failed to load local properties", ex);
                throw new RuntimeException("Failed to load local properties", ex);
            }
            log.error("S3ConfigLoader - Failed to load properties from S3", e);
            throw new RuntimeException("Failed to load properties from S3", e);
        }
    }
}

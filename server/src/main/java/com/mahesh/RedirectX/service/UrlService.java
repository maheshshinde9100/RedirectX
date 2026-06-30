package com.mahesh.RedirectX.service;

import com.mahesh.RedirectX.dto.UrlRequest;
import com.mahesh.RedirectX.dto.UrlResponse;
import com.mahesh.RedirectX.entity.Url;
import com.mahesh.RedirectX.repository.UrlCacheRepository;
import com.mahesh.RedirectX.repository.UrlRepository;
import com.mahesh.RedirectX.util.Base62;
import com.mahesh.RedirectX.util.SnowflakeIdGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class UrlService {
    
    private final UrlRepository urlRepository;
    private final UrlCacheRepository urlCacheRepository;
    private final SnowflakeIdGenerator snowflakeIdGenerator;
    
    @Value("${app.base-url}")
    private String baseUrl;
    
    @Value("${app.cache.ttl}")
    private long cacheTtl;
    
    @Transactional
    public UrlResponse createShortUrl(UrlRequest request) {
        log.info("Creating short URL for: {}", request.getOriginalUrl());
        
        // Check if custom alias is provided and already exists
        if (request.getCustomAlias() != null && !request.getCustomAlias().isEmpty()) {
            if (urlRepository.existsByCustomAlias(request.getCustomAlias())) {
                throw new IllegalArgumentException("Custom alias already exists");
            }
        }
        
        // Generate unique ID using Snowflake
        long uniqueId = snowflakeIdGenerator.nextId();
        
        // Generate short code using Base62
        String shortCode = request.getCustomAlias() != null && !request.getCustomAlias().isEmpty()
                ? request.getCustomAlias()
                : Base62.generateShortCode(uniqueId);
        
        // If custom alias is used, we still need to check if the generated short code exists
        if (request.getCustomAlias() == null || request.getCustomAlias().isEmpty()) {
            while (urlRepository.existsByShortCode(shortCode)) {
                uniqueId = snowflakeIdGenerator.nextId();
                shortCode = Base62.generateShortCode(uniqueId);
            }
        }
        
        // Build URL entity
        Url url = Url.builder()
                .id(uniqueId)
                .shortCode(shortCode)
                .originalUrl(request.getOriginalUrl())
                .customAlias(request.getCustomAlias())
                .userId(request.getUserId() != null ? request.getUserId() : 1L)
                .title(request.getTitle())
                .expiresAt(request.getExpiresAt())
                .active(true)
                .clickCount(0L)
                .build();
        
        // Save to database
        url = urlRepository.save(url);
        
        // Cache the URL
        urlCacheRepository.saveUrl(url, cacheTtl);
        
        log.info("Short URL created successfully: {}", shortCode);
        
        return UrlResponse.fromEntity(url, baseUrl);
    }
    
    @Transactional(readOnly = true)
    public UrlResponse getUrlByShortCode(String shortCode) {
        log.info("Fetching URL for short code: {}", shortCode);
        
        // Try to get from cache first
        Url url = urlCacheRepository.getUrlByShortCode(shortCode);
        
        if (url == null) {
            // If not in cache, fetch from database
            url = urlRepository.findByShortCode(shortCode)
                    .orElseThrow(() -> new IllegalArgumentException("URL not found"));
            
            // Cache it for future requests
            urlCacheRepository.saveUrl(url, cacheTtl);
        }
        
        // Check if URL is expired
        if (url.isExpired()) {
            throw new IllegalArgumentException("URL has expired");
        }
        
        // Check if URL is active
        if (!url.getActive()) {
            throw new IllegalArgumentException("URL is not active");
        }
        
        return UrlResponse.fromEntity(url, baseUrl);
    }
    
    @Transactional
    public void deleteUrl(String shortCode) {
        log.info("Deleting URL with short code: {}", shortCode);
        
        Url url = urlRepository.findByShortCode(shortCode)
                .orElseThrow(() -> new IllegalArgumentException("URL not found"));
        
        url.setActive(false);
        urlRepository.save(url);
        
        // Remove from cache
        urlCacheRepository.deleteUrl(shortCode);
        
        if (url.getCustomAlias() != null) {
            urlCacheRepository.deleteAlias(url.getCustomAlias());
        }
        
        log.info("URL deleted successfully: {}", shortCode);
    }
    
    @Transactional(readOnly = true)
    public java.util.List<UrlResponse> getUserUrls(Long userId) {
        log.info("Fetching URLs for user: {}", userId);
        
        return urlRepository.findByUserId(userId).stream()
                .map(url -> UrlResponse.fromEntity(url, baseUrl))
                .toList();
    }
    
    @Transactional
    public UrlResponse updateUrl(String shortCode, UrlRequest request) {
        log.info("Updating URL with short code: {}", shortCode);
        
        Url url = urlRepository.findByShortCode(shortCode)
                .orElseThrow(() -> new IllegalArgumentException("URL not found"));
        
        if (request.getOriginalUrl() != null) {
            url.setOriginalUrl(request.getOriginalUrl());
        }
        
        if (request.getTitle() != null) {
            url.setTitle(request.getTitle());
        }
        
        if (request.getExpiresAt() != null) {
            url.setExpiresAt(request.getExpiresAt());
        }
        
        url = urlRepository.save(url);
        
        // Update cache
        urlCacheRepository.saveUrl(url, cacheTtl);
        
        log.info("URL updated successfully: {}", shortCode);
        
        return UrlResponse.fromEntity(url, baseUrl);
    }
    
    @Transactional
    public void incrementClickCount(String shortCode) {
        log.debug("Incrementing click count for: {}", shortCode);
        
        // Increment in Redis for performance
        urlCacheRepository.incrementClickCount(shortCode);
        
        // Also update in database asynchronously (could be done via scheduled task)
        urlRepository.findByShortCode(shortCode).ifPresent(url -> {
            url.setClickCount(url.getClickCount() + 1);
            urlRepository.save(url);
        });
    }
}

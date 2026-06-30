package com.mahesh.RedirectX.service;

import com.mahesh.RedirectX.entity.Url;
import com.mahesh.RedirectX.repository.UrlCacheRepository;
import com.mahesh.RedirectX.repository.UrlRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class RedirectService {
    
    private final UrlRepository urlRepository;
    private final UrlCacheRepository urlCacheRepository;
    private final AnalyticsProducerService analyticsProducerService;
    
    @Value("${app.cache.ttl}")
    private long cacheTtl;
    
    @Transactional(readOnly = true)
    public String getOriginalUrl(String shortCode, String ipAddress, String userAgent, String referer) {
        log.info("Redirecting short code: {} from IP: {}", shortCode, ipAddress);
        
        // Try to get from cache first
        Url url = urlCacheRepository.getUrlByShortCode(shortCode);
        
        if (url == null) {
            // If not in cache, fetch from database
            url = urlRepository.findByShortCode(shortCode)
                    .orElse(null);
            
            if (url != null) {
                // Cache it for future requests
                urlCacheRepository.saveUrl(url, cacheTtl);
            }
        }
        
        if (url == null) {
            log.warn("URL not found for short code: {}", shortCode);
            throw new IllegalArgumentException("URL not found");
        }
        
        // Check if URL is expired
        if (url.isExpired()) {
            log.warn("URL has expired: {}", shortCode);
            throw new IllegalArgumentException("URL has expired");
        }
        
        // Check if URL is active
        if (!url.getActive()) {
            log.warn("URL is not active: {}", shortCode);
            throw new IllegalArgumentException("URL is not active");
        }
        
        // Increment click count
        urlCacheRepository.incrementClickCount(shortCode);
        
        // Send analytics event to Kafka (async)
        try {
            analyticsProducerService.sendAnalyticsEvent(url.getId(), shortCode, ipAddress, userAgent, referer);
        } catch (Exception e) {
            log.error("Failed to send analytics event", e);
            // Don't fail the redirect if analytics fails
        }
        
        log.info("Redirecting to: {}", url.getOriginalUrl());
        
        return url.getOriginalUrl();
    }
    
    @Transactional
    public void incrementClickCount(String shortCode) {
        log.debug("Incrementing click count for: {}", shortCode);
        
        // Increment in Redis for performance
        urlCacheRepository.incrementClickCount(shortCode);
        
        // Also update in database
        urlRepository.findByShortCode(shortCode).ifPresent(url -> {
            url.setClickCount(url.getClickCount() + 1);
            urlRepository.save(url);
        });
    }
}

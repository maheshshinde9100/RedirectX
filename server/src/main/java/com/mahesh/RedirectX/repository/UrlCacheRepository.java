package com.mahesh.RedirectX.repository;

import com.mahesh.RedirectX.entity.Url;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.concurrent.TimeUnit;

@Repository
public class UrlCacheRepository {
    
    private static final String URL_KEY_PREFIX = "url:";
    private static final String ALIAS_KEY_PREFIX = "alias:";
    private static final long DEFAULT_TTL = 3600; // 1 hour in seconds
    
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    
    public void saveUrl(Url url, long ttlSeconds) {
        String key = URL_KEY_PREFIX + url.getShortCode();
        redisTemplate.opsForValue().set(key, url, ttlSeconds, TimeUnit.SECONDS);
        
        if (url.getCustomAlias() != null) {
            String aliasKey = ALIAS_KEY_PREFIX + url.getCustomAlias();
            redisTemplate.opsForValue().set(aliasKey, url.getShortCode(), ttlSeconds, TimeUnit.SECONDS);
        }
    }
    
    public void saveUrl(Url url) {
        saveUrl(url, DEFAULT_TTL);
    }
    
    public Url getUrlByShortCode(String shortCode) {
        String key = URL_KEY_PREFIX + shortCode;
        return (Url) redisTemplate.opsForValue().get(key);
    }
    
    public String getShortCodeByAlias(String customAlias) {
        String key = ALIAS_KEY_PREFIX + customAlias;
        return (String) redisTemplate.opsForValue().get(key);
    }
    
    public void deleteUrl(String shortCode) {
        String key = URL_KEY_PREFIX + shortCode;
        redisTemplate.delete(key);
    }
    
    public void deleteAlias(String customAlias) {
        String key = ALIAS_KEY_PREFIX + customAlias;
        redisTemplate.delete(key);
    }
    
    public boolean existsUrl(String shortCode) {
        String key = URL_KEY_PREFIX + shortCode;
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }
    
    public boolean existsAlias(String customAlias) {
        String key = ALIAS_KEY_PREFIX + customAlias;
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }
    
    public void incrementClickCount(String shortCode) {
        String clickKey = "clicks:" + shortCode;
        redisTemplate.opsForValue().increment(clickKey);
    }
    
    public Long getClickCount(String shortCode) {
        String clickKey = "clicks:" + shortCode;
        Object value = redisTemplate.opsForValue().get(clickKey);
        return value != null ? Long.parseLong(value.toString()) : 0L;
    }
}

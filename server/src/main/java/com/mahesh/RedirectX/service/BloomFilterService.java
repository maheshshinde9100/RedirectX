package com.mahesh.RedirectX.service;

import com.google.common.hash.BloomFilter;
import com.google.common.hash.Funnels;
import com.mahesh.RedirectX.repository.UrlRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.charset.Charset;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class BloomFilterService {
    
    private final UrlRepository urlRepository;
    
    @Value("${app.bloom-filter.expected-insertions}")
    private long expectedInsertions;
    
    @Value("${app.bloom-filter.false-positive-probability}")
    private double falsePositiveProbability;
    
    private BloomFilter<String> shortCodeBloomFilter;
    private BloomFilter<String> customAliasBloomFilter;
    
    @PostConstruct
    public void init() {
        log.info("Initializing Bloom filters");
        
        // Create Bloom filters
        shortCodeBloomFilter = BloomFilter.create(
            Funnels.stringFunnel(Charset.defaultCharset()),
            expectedInsertions,
            falsePositiveProbability
        );
        
        customAliasBloomFilter = BloomFilter.create(
            Funnels.stringFunnel(Charset.defaultCharset()),
            expectedInsertions,
            falsePositiveProbability
        );
        
        // Load existing data into Bloom filters
        loadExistingData();
        
        log.info("Bloom filters initialized successfully");
    }
    
    private void loadExistingData() {
        log.info("Loading existing URLs into Bloom filters");
        
        // Load all short codes
        List<String> allShortCodes = urlRepository.findAll().stream()
                .map(com.mahesh.RedirectX.entity.Url::getShortCode)
                .toList();
        
        allShortCodes.forEach(shortCodeBloomFilter::put);
        
        // Load all custom aliases
        List<String> allCustomAliases = urlRepository.findAll().stream()
                .map(com.mahesh.RedirectX.entity.Url::getCustomAlias)
                .filter(alias -> alias != null && !alias.isEmpty())
                .toList();
        
        allCustomAliases.forEach(customAliasBloomFilter::put);
        
        log.info("Loaded {} short codes and {} custom aliases into Bloom filters",
                allShortCodes.size(), allCustomAliases.size());
    }
    
    public boolean mightContainShortCode(String shortCode) {
        return shortCodeBloomFilter.mightContain(shortCode);
    }
    
    public boolean mightContainCustomAlias(String customAlias) {
        return customAliasBloomFilter.mightContain(customAlias);
    }
    
    public void addShortCode(String shortCode) {
        shortCodeBloomFilter.put(shortCode);
        log.debug("Added short code to Bloom filter: {}", shortCode);
    }
    
    public void addCustomAlias(String customAlias) {
        customAliasBloomFilter.put(customAlias);
        log.debug("Added custom alias to Bloom filter: {}", customAlias);
    }
    
    public void removeShortCode(String shortCode) {
        // Bloom filters don't support removal, so we need to rebuild
        // In production, you might use a counting Bloom filter or rebuild periodically
        log.warn("Bloom filter doesn't support removal. Consider rebuilding the filter.");
    }
}

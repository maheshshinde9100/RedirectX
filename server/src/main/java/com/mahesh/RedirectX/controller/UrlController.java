package com.mahesh.RedirectX.controller;

import com.mahesh.RedirectX.annotation.RateLimit;
import com.mahesh.RedirectX.dto.UrlRequest;
import com.mahesh.RedirectX.dto.UrlResponse;
import com.mahesh.RedirectX.service.UrlService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/urls")
@RequiredArgsConstructor
@Slf4j
public class UrlController {
    
    private final UrlService urlService;
    
    @PostMapping
    @RateLimit
    public ResponseEntity<UrlResponse> createShortUrl(@Valid @RequestBody UrlRequest request) {
        log.info("Creating short URL request: {}", request.getOriginalUrl());
        UrlResponse response = urlService.createShortUrl(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    @GetMapping("/{shortCode}")
    public ResponseEntity<UrlResponse> getUrlByShortCode(@PathVariable String shortCode) {
        log.info("Fetching URL for short code: {}", shortCode);
        UrlResponse response = urlService.getUrlByShortCode(shortCode);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<UrlResponse>> getUserUrls(@PathVariable Long userId) {
        log.info("Fetching URLs for user: {}", userId);
        List<UrlResponse> responses = urlService.getUserUrls(userId);
        return ResponseEntity.ok(responses);
    }
    
    @PutMapping("/{shortCode}")
    public ResponseEntity<UrlResponse> updateUrl(
            @PathVariable String shortCode,
            @Valid @RequestBody UrlRequest request) {
        log.info("Updating URL with short code: {}", shortCode);
        UrlResponse response = urlService.updateUrl(shortCode, request);
        return ResponseEntity.ok(response);
    }
    
    @DeleteMapping("/{shortCode}")
    public ResponseEntity<Void> deleteUrl(@PathVariable String shortCode) {
        log.info("Deleting URL with short code: {}", shortCode);
        urlService.deleteUrl(shortCode);
        return ResponseEntity.noContent().build();
    }
}

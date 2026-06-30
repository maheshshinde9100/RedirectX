package com.mahesh.RedirectX.controller;

import com.mahesh.RedirectX.dto.AnalyticsResponse;
import com.mahesh.RedirectX.service.AnalyticsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/analytics")
@RequiredArgsConstructor
@Slf4j
public class AnalyticsController {
    
    private final AnalyticsService analyticsService;
    
    @GetMapping("/url/{urlId}")
    public ResponseEntity<AnalyticsResponse> getUrlAnalytics(@PathVariable Long urlId) {
        log.info("Fetching analytics for URL ID: {}", urlId);
        AnalyticsResponse response = analyticsService.getUrlAnalytics(urlId);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/short/{shortCode}")
    public ResponseEntity<AnalyticsResponse> getUrlAnalyticsByShortCode(@PathVariable String shortCode) {
        log.info("Fetching analytics for short code: {}", shortCode);
        AnalyticsResponse response = analyticsService.getUrlAnalyticsByShortCode(shortCode);
        return ResponseEntity.ok(response);
    }
}

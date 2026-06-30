package com.mahesh.RedirectX.service;

import com.mahesh.RedirectX.dto.AnalyticsResponse;
import com.mahesh.RedirectX.repository.AnalyticsRepository;
import com.mahesh.RedirectX.repository.UrlRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AnalyticsService {
    
    private final AnalyticsRepository analyticsRepository;
    private final UrlRepository urlRepository;
    
    @Transactional(readOnly = true)
    public AnalyticsResponse getUrlAnalytics(Long urlId) {
        log.info("Fetching analytics for URL ID: {}", urlId);
        
        // Verify URL exists
        urlRepository.findById(urlId)
                .orElseThrow(() -> new IllegalArgumentException("URL not found"));
        
        // Get total clicks
        long totalClicks = analyticsRepository.countByUrlId(urlId);
        
        // Get clicks by country
        List<Map<String, Object>> countryData = analyticsRepository.getClicksByCountry(urlId);
        Map<String, Long> clicksByCountry = countryData.stream()
                .collect(Collectors.toMap(
                        row -> (String) row.get("country"),
                        row -> ((Number) row.get("count")).longValue()
                ));
        
        // Get clicks by city
        List<Map<String, Object>> cityData = analyticsRepository.getClicksByCity(urlId);
        Map<String, Long> clicksByCity = cityData.stream()
                .collect(Collectors.toMap(
                        row -> (String) row.get("city"),
                        row -> ((Number) row.get("count")).longValue()
                ));
        
        // Get clicks by device
        List<Map<String, Object>> deviceData = analyticsRepository.getClicksByDevice(urlId);
        Map<String, Long> clicksByDevice = deviceData.stream()
                .collect(Collectors.toMap(
                        row -> (String) row.get("deviceType"),
                        row -> ((Number) row.get("count")).longValue()
                ));
        
        // Get clicks by browser
        List<Map<String, Object>> browserData = analyticsRepository.getClicksByBrowser(urlId);
        Map<String, Long> clicksByBrowser = browserData.stream()
                .collect(Collectors.toMap(
                        row -> (String) row.get("browser"),
                        row -> ((Number) row.get("count")).longValue()
                ));
        
        // Get clicks by date
        List<Map<String, Object>> dateData = analyticsRepository.getClicksByDate(urlId);
        Map<String, Long> clicksByDate = dateData.stream()
                .collect(Collectors.toMap(
                        row -> row.get("date").toString(),
                        row -> ((Number) row.get("count")).longValue()
                ));
        
        String shortCode = urlRepository.findById(urlId)
                .map(url -> url.getShortCode())
                .orElse("unknown");
        
        return AnalyticsResponse.builder()
                .urlId(urlId)
                .shortCode(shortCode)
                .totalClicks(totalClicks)
                .clicksByCountry(clicksByCountry)
                .clicksByCity(clicksByCity)
                .clicksByDevice(clicksByDevice)
                .clicksByBrowser(clicksByBrowser)
                .clicksByDate(clicksByDate)
                .build();
    }
    
    @Transactional(readOnly = true)
    public AnalyticsResponse getUrlAnalyticsByShortCode(String shortCode) {
        log.info("Fetching analytics for short code: {}", shortCode);
        
        return urlRepository.findByShortCode(shortCode)
                .map(url -> getUrlAnalytics(url.getId()))
                .orElseThrow(() -> new IllegalArgumentException("URL not found"));
    }
}

package com.mahesh.RedirectX.service;

import com.mahesh.RedirectX.dto.AnalyticsEvent;
import com.mahesh.RedirectX.entity.Analytics;
import com.mahesh.RedirectX.repository.AnalyticsRepository;
import com.mahesh.RedirectX.util.UserAgentParser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AnalyticsConsumerService {
    
    private final AnalyticsRepository analyticsRepository;
    private final GeoLocationService geoLocationService;
    private final UserAgentParser userAgentParser;
    
    @KafkaListener(
        topics = "${app.kafka.topic.analytics:analytics-events}",
        groupId = "${spring.kafka.consumer.group-id:analytics-group}",
        containerFactory = "kafkaListenerContainerFactory"
    )
    @Transactional
    public void consumeAnalyticsEvent(AnalyticsEvent event) {
        log.info("Consuming analytics event for short code: {}", event.getShortCode());
        
        try {
            // Parse user agent for device/browser/OS info
            userAgentParser.parse(event.getUserAgent()).ifPresent(parsedInfo -> {
                event.setDeviceType(parsedInfo.getDeviceType());
                event.setBrowser(parsedInfo.getBrowser());
                event.setOs(parsedInfo.getOs());
            });
            
            // Get geo location from IP address
            if (event.getIpAddress() != null && !event.getIpAddress().isEmpty()) {
                geoLocationService.getLocation(event.getIpAddress()).ifPresent(geoInfo -> {
                    event.setCountry(geoInfo.getCountry());
                    event.setCity(geoInfo.getCity());
                    event.setRegion(geoInfo.getRegion());
                });
            }
            
            // Save analytics to database
            Analytics analytics = Analytics.builder()
                    .urlId(event.getUrlId())
                    .shortCode(event.getShortCode())
                    .ipAddress(event.getIpAddress())
                    .userAgent(event.getUserAgent())
                    .referer(event.getReferer())
                    .country(event.getCountry())
                    .city(event.getCity())
                    .region(event.getRegion())
                    .clickedAt(event.getClickedAt())
                    .deviceType(event.getDeviceType())
                    .browser(event.getBrowser())
                    .os(event.getOs())
                    .build();
            
            analyticsRepository.save(analytics);
            
            log.debug("Analytics event saved successfully for short code: {}", event.getShortCode());
            
        } catch (Exception e) {
            log.error("Error processing analytics event for short code: {}", event.getShortCode(), e);
            // In production, you might want to send to a dead-letter queue
        }
    }
}

package com.mahesh.RedirectX.service;

import com.mahesh.RedirectX.dto.AnalyticsEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
@Slf4j
public class AnalyticsProducerService {
    
    private final KafkaTemplate<String, AnalyticsEvent> kafkaTemplate;
    
    @Value("${app.kafka.topic.analytics:analytics-events}")
    private String analyticsTopic;
    
    public void sendAnalyticsEvent(Long urlId, String shortCode, String ipAddress, String userAgent, String referer) {
        AnalyticsEvent event = AnalyticsEvent.builder()
                .urlId(urlId)
                .shortCode(shortCode)
                .ipAddress(ipAddress)
                .userAgent(userAgent)
                .referer(referer)
                .clickedAt(LocalDateTime.now())
                .build();
        
        sendAnalyticsEvent(event);
    }
    
    public void sendAnalyticsEvent(AnalyticsEvent event) {
        try {
            CompletableFuture<SendResult<String, AnalyticsEvent>> future = 
                kafkaTemplate.send(analyticsTopic, event.getShortCode(), event);
            
            future.whenComplete((result, ex) -> {
                if (ex == null) {
                    log.debug("Analytics event sent successfully for short code: {}", event.getShortCode());
                } else {
                    log.error("Failed to send analytics event for short code: {}", event.getShortCode(), ex);
                }
            });
        } catch (Exception e) {
            log.error("Error sending analytics event", e);
        }
    }
}

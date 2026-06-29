package com.mahesh.RedirectX.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnalyticsEvent {
    
    private Long urlId;
    private String shortCode;
    private String ipAddress;
    private String userAgent;
    private String referer;
    private String country;
    private String city;
    private String region;
    private LocalDateTime clickedAt;
    private String deviceType;
    private String browser;
    private String os;
}

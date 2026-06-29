package com.mahesh.RedirectX.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnalyticsResponse {
    
    private Long urlId;
    private String shortCode;
    private Long totalClicks;
    private Map<String, Long> clicksByCountry;
    private Map<String, Long> clicksByCity;
    private Map<String, Long> clicksByDevice;
    private Map<String, Long> clicksByBrowser;
    private Map<String, Long> clicksByDate;
}

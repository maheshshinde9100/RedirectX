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
public class UrlResponse {
    
    private Long id;
    private String shortCode;
    private String shortUrl;
    private String originalUrl;
    private String customAlias;
    private Long userId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime expiresAt;
    private Long clickCount;
    private Boolean active;
    private String title;
    
    public static UrlResponse fromEntity(com.mahesh.RedirectX.entity.Url url, String baseUrl) {
        return UrlResponse.builder()
                .id(url.getId())
                .shortCode(url.getShortCode())
                .shortUrl(baseUrl + "/" + url.getShortCode())
                .originalUrl(url.getOriginalUrl())
                .customAlias(url.getCustomAlias())
                .userId(url.getUserId())
                .createdAt(url.getCreatedAt())
                .updatedAt(url.getUpdatedAt())
                .expiresAt(url.getExpiresAt())
                .clickCount(url.getClickCount())
                .active(url.getActive())
                .title(url.getTitle())
                .build();
    }
}

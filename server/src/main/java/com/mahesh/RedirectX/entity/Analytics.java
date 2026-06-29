package com.mahesh.RedirectX.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "analytics", indexes = {
    @Index(name = "idx_url_id", columnList = "urlId"),
    @Index(name = "idx_clicked_at", columnList = "clickedAt"),
    @Index(name = "idx_country", columnList = "country"),
    @Index(name = "idx_city", columnList = "city")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Analytics {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private Long urlId;
    
    @Column(nullable = false)
    private String shortCode;
    
    @Column(nullable = false, length = 45)
    private String ipAddress;
    
    @Column(length = 100)
    private String userAgent;
    
    @Column(length = 100)
    private String referer;
    
    @Column(length = 2)
    private String country;
    
    @Column(length = 100)
    private String city;
    
    @Column(length = 100)
    private String region;
    
    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime clickedAt;
    
    @Column(length = 20)
    private String deviceType;
    
    @Column(length = 20)
    private String browser;
    
    @Column(length = 20)
    private String os;
}

package com.mahesh.RedirectX.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "urls", indexes = {
    @Index(name = "idx_short_code", columnList = "shortCode"),
    @Index(name = "idx_custom_alias", columnList = "customAlias"),
    @Index(name = "idx_expires_at", columnList = "expiresAt")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Url {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true)
    private String shortCode;
    
    @Column(nullable = false, length = 2048)
    private String originalUrl;
    
    @Column(unique = true)
    private String customAlias;
    
    @Column(nullable = false)
    private Long userId;
    
    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;
    
    @Column
    private LocalDateTime expiresAt;
    
    @Column(nullable = false)
    @Builder.Default
    private Long clickCount = 0L;
    
    @Column(nullable = false)
    @Builder.Default
    private Boolean active = true;
    
    @Column
    private String title;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    public boolean isExpired() {
        return expiresAt != null && LocalDateTime.now().isAfter(expiresAt);
    }
}

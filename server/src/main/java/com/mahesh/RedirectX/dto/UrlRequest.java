package com.mahesh.RedirectX.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UrlRequest {
    
    @NotBlank(message = "Original URL is required")
    @Pattern(regexp = "^(http|https)://.*", message = "URL must start with http:// or https://")
    @Size(max = 2048, message = "URL must be less than 2048 characters")
    private String originalUrl;
    
    @Size(min = 3, max = 50, message = "Custom alias must be between 3 and 50 characters")
    @Pattern(regexp = "^[a-zA-Z0-9-_]+$", message = "Custom alias can only contain letters, numbers, hyphens, and underscores")
    private String customAlias;
    
    @Size(max = 200, message = "Title must be less than 200 characters")
    private String title;
    
    private LocalDateTime expiresAt;
    
    private Long userId;
}

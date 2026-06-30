package com.mahesh.RedirectX.controller;

import com.mahesh.RedirectX.service.RedirectService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Slf4j
public class RedirectController {
    
    private final RedirectService redirectService;
    
    @GetMapping("/{shortCode}")
    public ResponseEntity<Void> redirect(
            @PathVariable String shortCode,
            HttpServletRequest request) {
        
        String ipAddress = getClientIpAddress(request);
        String userAgent = request.getHeader("User-Agent");
        String referer = request.getHeader("Referer");
        
        log.info("Redirect request for short code: {} from IP: {}", shortCode, ipAddress);
        
        try {
            String originalUrl = redirectService.getOriginalUrl(shortCode, ipAddress, userAgent, referer);
            return ResponseEntity.status(HttpStatus.FOUND)
                    .header("Location", originalUrl)
                    .build();
        } catch (IllegalArgumentException e) {
            log.error("Redirect failed for short code: {}", shortCode, e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
    
    private String getClientIpAddress(HttpServletRequest request) {
        String ipAddress = request.getHeader("X-Forwarded-For");
        if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("Proxy-Client-IP");
        }
        if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getRemoteAddr();
        }
        
        // For multiple proxies, take the first IP
        if (ipAddress != null && ipAddress.contains(",")) {
            ipAddress = ipAddress.split(",")[0].trim();
        }
        
        return ipAddress;
    }
}

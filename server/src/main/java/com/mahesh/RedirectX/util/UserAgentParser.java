package com.mahesh.RedirectX.util;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class UserAgentParser {
    
    private static final Pattern MOBILE_PATTERN = Pattern.compile(
        "(Mobile|Android|iPhone|iPad|iPod|BlackBerry|Windows Phone|webOS|Opera Mini)", 
        Pattern.CASE_INSENSITIVE
    );
    
    private static final Pattern BROWSER_PATTERN = Pattern.compile(
        "(Chrome|Firefox|Safari|Edge|Opera|MSIE|Trident)", 
        Pattern.CASE_INSENSITIVE
    );
    
    private static final Pattern OS_PATTERN = Pattern.compile(
        "(Windows|Mac|MacOS|Linux|Android|iOS|iPhone|iPad)", 
        Pattern.CASE_INSENSITIVE
    );
    
    public Optional<ParsedInfo> parse(String userAgent) {
        if (userAgent == null || userAgent.isEmpty()) {
            return Optional.empty();
        }
        
        String deviceType = parseDeviceType(userAgent);
        String browser = parseBrowser(userAgent);
        String os = parseOS(userAgent);
        
        return Optional.of(ParsedInfo.builder()
                .deviceType(deviceType)
                .browser(browser)
                .os(os)
                .build());
    }
    
    private String parseDeviceType(String userAgent) {
        Matcher matcher = MOBILE_PATTERN.matcher(userAgent);
        return matcher.find() ? "Mobile" : "Desktop";
    }
    
    private String parseBrowser(String userAgent) {
        Matcher matcher = BROWSER_PATTERN.matcher(userAgent);
        if (matcher.find()) {
            String browser = matcher.group(1);
            if (browser.equalsIgnoreCase("Trident") || browser.equalsIgnoreCase("MSIE")) {
                return "Internet Explorer";
            }
            return browser;
        }
        return "Unknown";
    }
    
    private String parseOS(String userAgent) {
        Matcher matcher = OS_PATTERN.matcher(userAgent);
        if (matcher.find()) {
            String os = matcher.group(1);
            if (os.equalsIgnoreCase("iPhone") || os.equalsIgnoreCase("iPad")) {
                return "iOS";
            }
            return os;
        }
        return "Unknown";
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ParsedInfo {
        private String deviceType;
        private String browser;
        private String os;
    }
}

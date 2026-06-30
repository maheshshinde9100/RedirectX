package com.mahesh.RedirectX.controller;

import com.mahesh.RedirectX.service.QRCodeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/qrcode")
@RequiredArgsConstructor
@Slf4j
public class QRCodeController {
    
    private final QRCodeService qrCodeService;
    
    @GetMapping("/{shortCode}")
    public ResponseEntity<byte[]> generateQRCode(@PathVariable String shortCode) {
        log.info("Generating QR code for short code: {}", shortCode);
        
        try {
            byte[] qrCode = qrCodeService.generateQRCode(shortCode);
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.IMAGE_PNG);
            headers.setContentLength(qrCode.length);
            headers.setContentDispositionFormData("attachment", shortCode + ".png");
            
            return new ResponseEntity<>(qrCode, headers, HttpStatus.OK);
        } catch (Exception e) {
            log.error("Error generating QR code for short code: {}", shortCode, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}

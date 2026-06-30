package com.mahesh.RedirectX.service;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Service
@Slf4j
public class QRCodeService {
    
    @Value("${app.base-url}")
    private String baseUrl;
    
    private static final int QR_CODE_WIDTH = 300;
    private static final int QR_CODE_HEIGHT = 300;
    
    public byte[] generateQRCode(String shortCode) {
        String shortUrl = baseUrl + "/" + shortCode;
        return generateQRCodeForUrl(shortUrl);
    }
    
    public byte[] generateQRCodeForUrl(String url) {
        try {
            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            BitMatrix bitMatrix = qrCodeWriter.encode(url, BarcodeFormat.QR_CODE, QR_CODE_WIDTH, QR_CODE_HEIGHT);
            
            ByteArrayOutputStream pngOutputStream = new ByteArrayOutputStream();
            MatrixToImageWriter.writeToStream(bitMatrix, "PNG", pngOutputStream);
            
            log.debug("QR code generated for URL: {}", url);
            return pngOutputStream.toByteArray();
            
        } catch (WriterException | IOException e) {
            log.error("Error generating QR code for URL: {}", url, e);
            throw new RuntimeException("Failed to generate QR code", e);
        }
    }
}

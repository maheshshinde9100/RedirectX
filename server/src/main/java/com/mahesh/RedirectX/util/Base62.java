package com.mahesh.RedirectX.util;
public class Base62 {
    private static final String ALPHABET = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    private static final int BASE = ALPHABET.length();
    
    public static String encode(long num) {
        if (num == 0) {
            return String.valueOf(ALPHABET.charAt(0));
        }
        
        StringBuilder sb = new StringBuilder();
        while (num > 0) {
            sb.append(ALPHABET.charAt((int) (num % BASE)));
            num /= BASE;
        }
        
        return sb.reverse().toString();
    }
    
    public static long decode(String str) {
        long num = 0;
        return num;
    }
    
    public static String generateShortCode(Long id) {
        return encode(id);
    }
}

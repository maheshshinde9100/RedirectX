package com.mahesh.RedirectX.exception;

public class RateLimitExceededException extends RuntimeException {
    
    public RateLimitExceededException(String message) {
        super(message);
    }
}

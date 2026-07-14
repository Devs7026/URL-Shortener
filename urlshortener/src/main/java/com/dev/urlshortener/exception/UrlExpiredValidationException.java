package com.dev.urlshortener.exception;

public class UrlExpiredValidationException extends RuntimeException {

    public UrlExpiredValidationException(String message) {
        super(message);
    }
}

package com.demo.service;

/**
 * Thrown when order creation request fails validation.
 * Enables clients to distinguish business rule violations from system errors.
 */
public class InvalidOrderException extends RuntimeException {
    public InvalidOrderException(String message) {
        super(message);
    }

    public InvalidOrderException(String message, Throwable cause) {
        super(message, cause);
    }
}


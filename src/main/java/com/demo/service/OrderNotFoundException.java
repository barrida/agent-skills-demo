package com.demo.service;

/**
 * Thrown when an order is not found in the repository.
 * Enables proper 404 HTTP response handling instead of 500 errors.
 */
public class OrderNotFoundException extends RuntimeException {
    public OrderNotFoundException(String message) {
        super(message);
    }

    public OrderNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}


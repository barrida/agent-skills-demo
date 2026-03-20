package com.demo.service;

import java.math.BigDecimal;

/**
 * Request DTO for creating order items.
 * Replaces untyped Map<String, Object> with type-safe, self-documenting structure.
 */
public record CreateOrderItemRequest(
    String productId,
    String productName,
    Integer quantity,
    BigDecimal price
) {
    /**
     * Validates order item request data.
     *
     * @throws IllegalArgumentException if any field is invalid
     */
    public void validate() {
        if (productId == null || productId.isBlank()) {
            throw new IllegalArgumentException("productId cannot be null or empty");
        }
        if (productName == null || productName.isBlank()) {
            throw new IllegalArgumentException("productName cannot be null or empty");
        }
        if (quantity == null || quantity <= 0) {
            throw new IllegalArgumentException("quantity must be greater than 0");
        }
        if (price == null || price.signum() <= 0) {
            throw new IllegalArgumentException("price must be greater than 0");
        }
    }
}


package com.demo.model;

/**
 * Enumeration of valid order statuses.
 * Replaces String-based status flags to ensure type safety and prevent typos.
 */
public enum OrderStatus {
    PENDING,
    SHIPPED,
    DELIVERED,
    CANCELLED;

    /**
     * Determines if an order with this status can be cancelled.
     * Orders that are already shipped or delivered cannot be cancelled.
     *
     * @return true if order can be cancelled, false otherwise
     */
    public boolean isCancellable() {
        return this != SHIPPED && this != DELIVERED;
    }
}


package com.demo.controller;

import com.demo.service.CreateOrderItemRequest;
import java.util.List;

/**
 * Request DTO for creating an order via REST API.
 * Provides strong typing and validation at the API boundary.
 */
public record CreateOrderRequest(
    String customerId,
    List<CreateOrderItemRequest> items
) { }


package com.demo.controller;

import com.demo.model.Order;
import com.demo.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    // Missing: request validation, error handling, proper DTOs (exposing domain model directly)
    @PostMapping
    public Order createOrder(@RequestBody Map<String, Object> payload) {
        String customerId = (String) payload.get("customerId");
        List<Map<String, Object>> items = (List<Map<String, Object>>) payload.get("items");
        return orderService.placeOrder(customerId, items);  // no try-catch, raw exception propagation
    }

    @GetMapping("/{id}")
    public Order getOrder(@PathVariable Long id) {
        return orderService.getOrder(id);  // will throw 500 if not found instead of 404
    }

    @GetMapping
    public List<Order> getByCustomer(@RequestParam String customerId) {
        return orderService.getOrdersByCustomer(customerId);
    }

    // Non-RESTful: action in URL instead of using PATCH with status
    @PostMapping("/{id}/cancel")
    public Order cancelOrder(@PathVariable Long id) {
        return orderService.cancelOrder(id);
    }

    @GetMapping("/{id}/total")
    public BigDecimal recalcTotal(@PathVariable Long id) {
        return orderService.recalculateTotal(id);
    }
}

package com.demo.controller;

import com.demo.model.Order;
import com.demo.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    /**
     * Creates a new order from a typed request DTO.
     * Request validation happens via @Valid annotation.
     *
     * @param request the create order request with customer ID and items
     * @return ResponseEntity with created order and 201 Created status
     */
    @PostMapping
    public ResponseEntity<Order> createOrder(@Valid @RequestBody CreateOrderRequest request) {
        Order order = orderService.placeOrder(request.customerId(), request.items());
        return ResponseEntity.status(HttpStatus.CREATED).body(order);
    }

    /**
     * Retrieves an order by ID.
     * Returns 404 Not Found if order does not exist (handled by global exception handler).
     *
     * @param id the order ID
     * @return the order
     */
    @GetMapping("/{id}")
    public Order getOrder(@PathVariable Long id) {
        return orderService.getOrder(id);
    }

    /**
     * Retrieves all orders for a specific customer.
     *
     * @param customerId the customer ID
     * @return list of orders for the customer
     */
    @GetMapping
    public List<Order> getByCustomer(@RequestParam String customerId) {
        return orderService.getOrdersByCustomer(customerId);
    }

    /**
     * Cancels an order.
     * Non-RESTful action endpoint (consider using PATCH /orders/{id} with status body in future).
     *
     * @param id the order ID
     * @return the cancelled order
     */
    @PostMapping("/{id}/cancel")
    public Order cancelOrder(@PathVariable Long id) {
        return orderService.cancelOrder(id);
    }

    /**
     * Recalculates the total for an order.
     *
     * @param id the order ID
     * @return the recalculated total
     */
    @GetMapping("/{id}/total")
    public BigDecimal recalcTotal(@PathVariable Long id) {
        return orderService.recalculateTotal(id);
    }
}

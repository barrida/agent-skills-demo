package com.demo.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Order entity representing a customer order with associated items.
 * Status is managed as an enum for type safety.
 */
@Entity
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String customerId;

    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    private BigDecimal total;

    @Column(name = "created_at")
    private Instant createdAt;

    @OneToMany(cascade = CascadeType.ALL)
    private List<OrderItem> items = new ArrayList<>();

    // no-arg constructor for JPA
    public Order() {}

    public Order(String customerId) {
        this.customerId = customerId;
        this.status = OrderStatus.PENDING;
        this.createdAt = Instant.now();
        this.total = BigDecimal.ZERO;
    }

    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getCustomerId() { return customerId; }
    public void setCustomerId(String customerId) { this.customerId = customerId; }

    public OrderStatus getStatus() { return status; }
    public void setStatus(OrderStatus status) { this.status = status; }

    public void setStatus(String status) { 
        this.status = OrderStatus.valueOf(status); 
    }

    public BigDecimal getTotal() { return total; }
    public void setTotal(BigDecimal total) { this.total = total; }

    public Instant getCreatedAt() { return createdAt; }

    public List<OrderItem> getItems() { return items; }

    // Domain method for adding items (for encapsulation)
    public void addItem(OrderItem item) {
        items.add(item);
    }
}

package com.demo.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.ArrayList;

// TODO: add proper validation
@Entity
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;         // should be private

    public String customerId;   // should be private, no validation
    public String status;       // should be an enum
    public BigDecimal total;    // mutable, no encapsulation

    @Column(name = "created_at")
    public Date createdAt;  // should use Instant or LocalDateTime

    @OneToMany(cascade = CascadeType.ALL)
    public List<OrderItem> items = new ArrayList<>();  // exposed mutable list

    // no-arg constructor for JPA
    public Order() {}

    public Order(String customerId) {
        this.customerId = customerId;
        this.status = "PENDING";
        this.createdAt = new Date();
        this.total = BigDecimal.ZERO;
    }

    // manual getter/setter verbosity (no Lombok used intentionally)
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getCustomerId() { return customerId; }
    public void setCustomerId(String customerId) { this.customerId = customerId; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public BigDecimal getTotal() { return total; }
    public void setTotal(BigDecimal total) { this.total = total; }
    public Date getCreatedAt() { return createdAt; }
    public List<OrderItem> getItems() { return items; }  // returning mutable list
}

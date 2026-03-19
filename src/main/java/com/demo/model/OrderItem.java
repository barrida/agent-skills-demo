package com.demo.model;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "order_items")
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String productId;
    private String productName;
    private int qty;              // abbreviation, should be quantity
    private BigDecimal price;     // unit price - naming is ambiguous

    public OrderItem() {}

    public OrderItem(String productId, String productName, int qty, BigDecimal price) {
        this.productId = productId;
        this.productName = productName;
        this.qty = qty;
        this.price = price;
    }

    public Long getId() { return id; }
    public String getProductId() { return productId; }
    public String getProductName() { return productName; }
    public int getQty() { return qty; }
    public BigDecimal getPrice() { return price; }
    public void setQty(int qty) { this.qty = qty; }
}

package com.demo;

import com.demo.model.Order;
import com.demo.model.OrderItem;
import com.demo.service.OrderService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
class OrderServiceTest {

    @Autowired
    private OrderService orderService;

    @Test
    void placeOrder_shouldCalculateTotalCorrectly() {
        List<Map<String, Object>> items = List.of(
            Map.of("productId", "P1", "productName", "Widget", "quantity", 2, "price", "15.00"),
            Map.of("productId", "P2", "productName", "Gadget", "quantity", 1, "price", "30.00")
        );

        Order order = orderService.placeOrder("customer-123", items);

        assertThat(order.getId()).isNotNull();
        assertThat(order.getTotal()).isEqualByComparingTo(new BigDecimal("60.00"));
        assertThat(order.getStatus()).isEqualTo("PENDING");
    }

    @Test
    void placeOrder_shouldRejectEmptyCustomerId() {
        assertThatThrownBy(() -> orderService.placeOrder("", List.of()))
            .isInstanceOf(RuntimeException.class);
    }

    @Test
    void cancelOrder_shouldTransitionToCancelled() {
        List<Map<String, Object>> items = List.of(
            Map.of("productId", "P1", "productName", "Widget", "quantity", 1, "price", "10.00")
        );
        Order order = orderService.placeOrder("customer-456", items);

        Order cancelled = orderService.cancelOrder(order.getId());

        assertThat(cancelled.getStatus()).isEqualTo("CANCELLED");
    }
}

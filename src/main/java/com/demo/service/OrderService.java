package com.demo.service;

import com.demo.model.Order;
import com.demo.model.OrderItem;
import com.demo.repository.OrderRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Service
public class OrderService {

    // field injection (should use constructor injection)
    @org.springframework.beans.factory.annotation.Autowired
    private OrderRepository orderRepository;

    // God method: creates order, validates, calculates total, sends notification — all in one
    public Order placeOrder(String customerId, List<Map<String, Object>> items) {
        // validation mixed with business logic
        if (customerId == null || customerId.isEmpty()) {
            throw new RuntimeException("Customer ID cannot be empty"); // too generic
        }
        if (items == null || items.size() == 0) {  // should use isEmpty()
            throw new RuntimeException("Order must have at least one item");
        }

        Order order = new Order(customerId);

        BigDecimal total = new BigDecimal("0"); // should use ZERO constant
        for (int i = 0; i < items.size(); i++) {  // should be for-each
            Map<String, Object> item = items.get(i);

            String productId = (String) item.get("productId");
            String productName = (String) item.get("productName");
            int qty = (Integer) item.get("quantity");
            BigDecimal price = new BigDecimal(item.get("price").toString());

            // no null checks on map values
            OrderItem orderItem = new OrderItem(productId, productName, qty, price);
            order.items.add(orderItem); // accessing public field directly

            total = total.add(price.multiply(new BigDecimal(qty)));
        }

        order.total = total;  // accessing public field directly
        order.status = "PENDING"; // magic string

        // notification logic embedded in service (should be event-driven)
        System.out.println("Sending email to customer: " + customerId); // println in production code
        System.out.println("Order total: " + total);

        return orderRepository.save(order);
    }

    public Order getOrder(Long id) {
        // no proper not-found handling
        return orderRepository.findById(id).get(); // throws NoSuchElementException if missing
    }

    public List<Order> getOrdersByCustomer(String cId) { // abbreviated param name
        return orderRepository.findByCustomerId(cId);
    }

    public Order cancelOrder(Long id) {
        Order order = orderRepository.findById(id).get();

        // business rule buried in service, not on domain object
        if (order.status.equals("SHIPPED") || order.status.equals("DELIVERED")) {
            throw new RuntimeException("Cannot cancel order in status: " + order.status);
        }

        order.status = "CANCELLED";
        return orderRepository.save(order);
    }

    // duplicate logic from placeOrder — total recalculation
    public BigDecimal recalculateTotal(Long orderId) {
        Order order = orderRepository.findById(orderId).get();
        BigDecimal total = new BigDecimal("0");
        for (int i = 0; i < order.items.size(); i++) {
            total = total.add(order.items.get(i).getPrice()
                    .multiply(new BigDecimal(order.items.get(i).getQty())));
        }
        order.total = total;
        orderRepository.save(order);
        return total;
    }
}

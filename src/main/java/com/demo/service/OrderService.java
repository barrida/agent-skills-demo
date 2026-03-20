package com.demo.service;

import com.demo.model.Order;
import com.demo.model.OrderItem;
import com.demo.model.OrderStatus;
import com.demo.repository.OrderRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
public class OrderService {

    private static final Logger log = LoggerFactory.getLogger(OrderService.class);

    private final OrderRepository orderRepository;

    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }


    /**
     * Creates and persists a new order with the provided items.
     * Validates input, builds order items, calculates total, and saves to repository.
     *
     * @param customerId the customer ID
     * @param items list of item maps containing productId, productName, quantity, and price
     * @return the created Order entity
     * @throws InvalidOrderException if validation fails
     */
    public Order placeOrder(String customerId, List<Map<String, Object>> items) {
        validatePlaceOrderRequest(customerId, items);

        Order order = new Order(customerId);
        order.setStatus(OrderStatus.PENDING);

        addItemsToOrder(order, items);
        BigDecimal total = calculateOrderTotal(order.getItems());
        order.setTotal(total);

        Order savedOrder = orderRepository.save(order);
        log.info("Order created with ID: {}, customer: {}, total: {}", savedOrder.getId(), customerId, total);

        return savedOrder;
    }

    /**
     * Validates the place order request.
     *
     * @param customerId the customer ID to validate
     * @param items the items list to validate
     * @throws InvalidOrderException if validation fails
     */
    private void validatePlaceOrderRequest(String customerId, List<Map<String, Object>> items) {
        if (customerId == null || customerId.isEmpty()) {
            throw new InvalidOrderException("Customer ID cannot be empty");
        }
        if (items == null || items.isEmpty()) {
            throw new InvalidOrderException("Order must have at least one item");
        }
    }

    /**
     * Adds items from the provided list to the order.
     * Validates and converts each item map to an OrderItem entity.
     *
     * @param order the order to add items to
     * @param items list of item maps
     * @throws InvalidOrderException if item validation fails
     */
    private void addItemsToOrder(Order order, List<Map<String, Object>> items) {
        for (Map<String, Object> item : items) {
            String productId = Objects.requireNonNull(
                    (String) item.get("productId"),
                    "productId is required");
            String productName = Objects.requireNonNull(
                    (String) item.get("productName"),
                    "productName is required");

            Object qtyObj = item.get("quantity");
            if (qtyObj == null) {
                throw new InvalidOrderException("quantity is required");
            }
            int qty = (Integer) qtyObj;

            Object priceObj = item.get("price");
            if (priceObj == null) {
                throw new InvalidOrderException("price is required");
            }
            BigDecimal price = new BigDecimal(priceObj.toString());

            OrderItem orderItem = new OrderItem(productId, productName, qty, price);
            order.getItems().add(orderItem);
        }
    }

    /**
     * Calculates the total price for a list of order items.
     *
     * @param items the order items to calculate total for
     * @return the total price
     */
    private BigDecimal calculateOrderTotal(List<OrderItem> items) {
        BigDecimal total = BigDecimal.ZERO;
        for (OrderItem item : items) {
            total = total.add(item.getPrice().multiply(new BigDecimal(item.getQty())));
        }
        return total;
    }


    /**
     * Retrieves an order by ID.
     *
     * @param id the order ID
     * @return the Order entity
     * @throws OrderNotFoundException if order does not exist
     */
    public Order getOrder(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new OrderNotFoundException("Order not found: " + id));
    }

    /**
     * Retrieves all orders for a specific customer.
     *
     * @param customerId the customer ID
     * @return list of orders for the customer
     */
    public List<Order> getOrdersByCustomer(String customerId) {
        return orderRepository.findByCustomerId(customerId);
    }

    /**
     * Cancels an order if it's in a cancellable state.
     * Orders that are shipped or delivered cannot be cancelled.
     *
     * @param id the order ID
     * @return the cancelled Order entity
     * @throws OrderNotFoundException if order does not exist
     * @throws InvalidOrderException if order cannot be cancelled
     */
    public Order cancelOrder(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new OrderNotFoundException("Order not found: " + id));

        if (!order.getStatus().isCancellable()) {
            throw new InvalidOrderException("Cannot cancel order in status: " + order.getStatus());
        }

        order.setStatus(OrderStatus.CANCELLED);
        return orderRepository.save(order);
    }

    /**
     * Recalculates and updates the total price for an order.
     *
     * @param orderId the order ID
     * @return the recalculated total
     * @throws OrderNotFoundException if order does not exist
     */
    public BigDecimal recalculateTotal(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException("Order not found: " + orderId));

        BigDecimal total = calculateOrderTotal(order.getItems());
        order.setTotal(total);
        orderRepository.save(order);

        log.info("Order {} total recalculated: {}", orderId, total);
        return total;
    }
}

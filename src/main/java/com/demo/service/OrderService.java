package com.demo.service;

import com.demo.model.Order;
import com.demo.model.OrderItem;
import com.demo.model.OrderStatus;
import com.demo.repository.OrderRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

/**
 * Service for managing orders.
 * Orchestrates business logic for order creation, retrieval, cancellation, and recalculation.
 * Uses constructor injection, proper exception handling, and transactional boundaries.
 */
@Service
@Transactional
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
     * @param items list of typed CreateOrderItemRequest objects
     * @return the created Order entity
     * @throws InvalidOrderException if validation fails
     */
    public Order placeOrder(String customerId, List<CreateOrderItemRequest> items) {
        validatePlaceOrderRequest(customerId, items);

        Order order = new Order(customerId);
        addItemsToOrder(order, items);
        
        BigDecimal total = calculateOrderTotal(order);
        order.setTotal(total);

        Order savedOrder = orderRepository.save(order);
        notifyOrderCreated(savedOrder, customerId, total);

        return savedOrder;
    }

    /**
     * Validates the place order request data.
     * Ensures customerId is not empty and items list is not empty.
     *
     * @param customerId the customer ID to validate
     * @param items the items list to validate
     * @throws InvalidOrderException if customerId is empty or items list is empty
     */
    private void validatePlaceOrderRequest(String customerId, List<CreateOrderItemRequest> items) {
        if (customerId == null || customerId.isBlank()) {
            throw new InvalidOrderException("Customer ID cannot be empty");
        }
        if (items == null || items.isEmpty()) {
            throw new InvalidOrderException("Order must have at least one item");
        }
    }

    /**
     * Adds validated items to the order.
     * Converts each CreateOrderItemRequest to an OrderItem and adds to order via domain method.
     *
     * @param order the order to add items to
     * @param items list of typed item requests
     * @throws InvalidOrderException if item validation fails
     */
    private void addItemsToOrder(Order order, List<CreateOrderItemRequest> items) {
        for (CreateOrderItemRequest itemRequest : items) {
            itemRequest.validate();
            
            OrderItem orderItem = new OrderItem(
                    itemRequest.productId(),
                    itemRequest.productName(),
                    itemRequest.quantity(),
                    itemRequest.price()
            );
            order.addItem(orderItem);
        }
    }

    /**
     * Calculates the total price for an order.
     * Sums the product of each item's unit price and quantity.
     *
     * @param order the order to calculate total for
     * @return the total price
     */
    private BigDecimal calculateOrderTotal(Order order) {
        return order.getItems().stream()
                .map(item -> item.getPrice().multiply(new BigDecimal(item.getQty())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
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
     * @return unmodifiable list of orders for the customer
     */
    public List<Order> getOrdersByCustomer(String customerId) {
        return Collections.unmodifiableList(orderRepository.findByCustomerId(customerId));
    }

    /**
     * Cancels an order if it is in a cancellable state.
     * Orders that are already shipped or delivered cannot be cancelled.
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
     * Useful when order items are modified externally.
     *
     * @param orderId the order ID
     * @return the recalculated total
     * @throws OrderNotFoundException if order does not exist
     */
    public BigDecimal recalculateTotal(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException("Order not found: " + orderId));

        BigDecimal total = calculateOrderTotal(order);
        order.setTotal(total);
        orderRepository.save(order);

        notifyOrderRecalculated(orderId, total);
        return total;
    }

    /**
     * Notifies customer of successful order creation.
     * Logs order details for audit trail.
     *
     * @param order the created order
     * @param customerId the customer ID
     * @param total the order total
     */
    private void notifyOrderCreated(Order order, String customerId, BigDecimal total) {
        log.info("Order created with ID: {}, customer: {}, total: {}", order.getId(), customerId, total);
    }

    /**
     * Notifies customer of order total recalculation.
     * Logs recalculation for audit trail.
     *
     * @param orderId the order ID
     * @param newTotal the recalculated total
     */
    private void notifyOrderRecalculated(Long orderId, BigDecimal newTotal) {
        log.info("Order {} total recalculated: {}", orderId, newTotal);
    }
}

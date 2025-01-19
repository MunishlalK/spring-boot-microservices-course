package com.bookstorelabs.order.domain;

import com.bookstorelabs.order.domain.models.CreateOrderRequest;
import com.bookstorelabs.order.domain.models.CreateOrderResponse;
import com.bookstorelabs.order.domain.models.OrderCreatedEvent;
import com.bookstorelabs.order.domain.models.OrderStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class OrderService {

    private final static Logger log = LoggerFactory.getLogger(OrderService.class);
    private final OrderRepository orderRepository;
    private final OrderValidator orderValidator;
    private final OrderEventService orderEventService;

    public OrderService(OrderRepository orderRepository,
                        OrderValidator orderValidator,
                        OrderEventService orderEventService) {
        this.orderRepository = orderRepository;
        this.orderValidator = orderValidator;
        this.orderEventService = orderEventService;
    }

    public CreateOrderResponse createOrder(String userName, CreateOrderRequest request) {
        orderValidator.validate(request);
        OrderEntity newOrder = OrderMapper.convertToEntity(request);
        newOrder.setUserName(userName);
        OrderEntity savedOrder = this.orderRepository.save(newOrder);
        log.info("Created Order with orderNumber={}", savedOrder.getOrderNumber());
        OrderCreatedEvent orderCreatedEvent = OrderEventMapper.buildOrderCreatedEvent(savedOrder);
        orderEventService.save(orderCreatedEvent);
        return new CreateOrderResponse(savedOrder.getOrderNumber());
    }

    public void processNewOrders() {
        List<OrderEntity> orders = orderRepository.findByStatus(OrderStatus.NEW);
        log.info("Found {} new orders to process", orders.size());
        for (OrderEntity order : orders) {
            this.process(order);
        }
    }

    private void process(OrderEntity order) {
    try {

    } catch(RuntimeException e) {
        log.error("Failed to process Order with OrderNumber: {}", order.getOrderNumber(),e);
        orderRepository.updateOrderStatus(order.getOrderNumber(), OrderStatus.ERROR);
        orderEventService.save(OrderEventMapper.buildOrderErrorEvent(order,e.getMessage()));
    }
    }

}

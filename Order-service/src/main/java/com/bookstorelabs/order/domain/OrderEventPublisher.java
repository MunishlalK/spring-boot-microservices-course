package com.bookstorelabs.order.domain;

import com.bookstorelabs.order.ApplicationProperties;
import com.bookstorelabs.order.domain.models.OrderCancelledEvent;
import com.bookstorelabs.order.domain.models.OrderCreatedEvent;
import com.bookstorelabs.order.domain.models.OrderDeliveredEvent;
import com.bookstorelabs.order.domain.models.OrderErrorEvent;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
class OrderEventPublisher {
    private final RabbitTemplate template;
    private final ApplicationProperties properties;

    OrderEventPublisher(RabbitTemplate template, ApplicationProperties properties) {
        this.template = template;
        this.properties = properties;
    }

    public void publish(OrderCreatedEvent event) {
        this.send(properties.newOrdersQueue(), event);
    }

    public void publish(OrderDeliveredEvent event) {
        this.send(properties.deliveredOrdersQueue(), event);
    }

    public void publish(OrderCancelledEvent event) {
        this.send(properties.cancelledOrdersQueue(), event);
    }

    public void publish(OrderErrorEvent event) {
        this.send(properties.errorOrdersQueue(),event);
    }

    public void send(String routingKey, Object payload) {
        template.convertAndSend(properties.orderEventsExchange(),routingKey,payload);
    }


}

package com.bookstorelabs.order.domain;

import com.bookstorelabs.order.ApplicationProperties;
import com.bookstorelabs.order.domain.models.OrderCreatedEvent;
import com.bookstorelabs.order.domain.models.OrderEventType;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class OrderEventService {
    private static final Logger log = LoggerFactory.getLogger(OrderEventService.class);
    private final ApplicationProperties properties;
    private final OrderEventRepository repository;
    private final OrderEventPublisher orderEventPublisher;
    private final ObjectMapper objectMapper;

    public OrderEventService(ApplicationProperties properties,
                             OrderEventRepository repository,
                             OrderEventPublisher orderEventPublisher,
                             ObjectMapper objectMapper) {
        this.properties = properties;
        this.repository = repository;
        this.orderEventPublisher = orderEventPublisher;
        this.objectMapper = objectMapper;
    }

    void save(OrderCreatedEvent event) {
        OrderEventEntity orderEvent = new OrderEventEntity();
        orderEvent.setEventId(event.eventId());
        orderEvent.setEventType(OrderEventType.ORDER_CREATED);
        orderEvent.setOrderNumber(event.orderNumber());
        orderEvent.setCreatedAt(event.createdAt());
        orderEvent.setPayload(toJsonPayload(event));

        this.repository.save(orderEvent);
    }

//    Sort sort = Sort.by("name").ascending();
//    pageNo = (pageNo <= 1) ? 0 : pageNo - 1;
//    Pageable pageable = PageRequest.of(pageNo, properties.pageSize(), sort);
//    Page<Product> productsPage = productRepository.findAll(pageable).map(ProductMapper::toProduct);

    public void publishOrderEvents() {
        Sort sort = Sort.by("createdAt").ascending();
        Pageable pageable = PageRequest.of(0, properties.pageSize(), sort);
        Page<OrderEventEntity> pageEvents = repository.findAll(pageable);
        List<OrderEventEntity> listEvents = pageEvents.getContent();
            listEvents.forEach(event -> {
            this.publishEvent(event);
            repository.delete(event);
        });

        while(pageEvents.hasNext()){
            pageEvents = repository.findAll(pageEvents.nextPageable());
            pageEvents.get().forEach(event -> {
                this.publishEvent(event);
                repository.delete(event);
            });
        };

    }

    private void publishEvent(OrderEventEntity event) {
        OrderEventType eventType = event.getEventType();
        switch(eventType) {
            case ORDER_CREATED:
                OrderCreatedEvent orderCreatedEvent = fromJsonPayload(event.getPayload(), OrderCreatedEvent.class);
                orderEventPublisher.publish(orderCreatedEvent);
                break;
        }

    }

    private String toJsonPayload(Object event) {
        try{
            return objectMapper.writeValueAsString(event);
        } catch (JsonProcessingException e){
            throw new RuntimeException(e);
        }
    }

    private <T> T fromJsonPayload(String json, Class<T> type) {
        try {
            return objectMapper.readValue(json, type);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }


}

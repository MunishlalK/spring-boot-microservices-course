package com.bookstorelabs.order.domain;

import com.bookstorelabs.order.domain.models.CreateOrderRequest;
import com.bookstorelabs.order.domain.models.OrderItem;
import com.bookstorelabs.order.domain.models.OrderStatus;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class OrderMapper {
    static OrderEntity convertToEntity(CreateOrderRequest request) {
        OrderEntity newOrder = new OrderEntity();
        newOrder.setOrderNumber(UUID.randomUUID().toString());
        newOrder.setStatus(OrderStatus.NEW);
        newOrder.setCustomer(request.customer());
        newOrder.setDeliveryAddress(request.deliveryAddress());
        //create OrderItemEntity
        Set<OrderItemEntity> orderItems = new HashSet<>();
        for (OrderItem item: request.items()){
            OrderItemEntity orderItem = new OrderItemEntity();
            orderItem.setCode(item.code());
            orderItem.setName(item.name());
            orderItem.setPrice(item.price());
            orderItem.setQuantity(item.quantity());
            //join orderItemEntity with OrderEntity
            orderItem.setOrder(newOrder);
            //add to hashSet
            orderItems.add(orderItem);}
        //create OrderItemEntity
        newOrder.setItems(orderItems);
        return newOrder;
    }
}

package com.bookstorelabs.order.domain;

import com.bookstorelabs.order.domain.models.OrderCreatedEvent;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderEventRepository extends JpaRepository<OrderEventEntity, Long> {
}

package com.bookstorelabs.order.domain;

import com.bookstorelabs.order.clients.catalog.Product;
import com.bookstorelabs.order.clients.catalog.ProductServiceClient;
import com.bookstorelabs.order.domain.models.CreateOrderRequest;
import com.bookstorelabs.order.domain.models.OrderItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
class OrderValidator {
    private static final Logger log = LoggerFactory.getLogger(OrderValidator.class);
    private final ProductServiceClient client;

    public OrderValidator(ProductServiceClient client) {
        this.client = client;
    }

   void validate(CreateOrderRequest request) {
        Set<OrderItem> items = request.items();
        for(OrderItem item: items) {
            Product product = client.getProductByCode(item.code())
                    .orElseThrow(() -> new InvalidOrderException("Invalid Product code:" + item.code()));
            if(item.price().compareTo(product.price()) != 0) {
                log.error(
                        "Product price not matching. Actual price:{}, received price: {}",
                        product.price(),
                        item.price());
                throw new InvalidOrderException("Product price not matching");

            }
        }
    }
}

package com.example.shoporder.listener;

import com.example.shopcore.config.RabbitMQConfig;
import com.example.shopinventory.service.InventoryService;
import com.example.shoporder.entity.Order;
import com.example.shoporder.entity.OrderItem;
import com.example.shoporder.entity.OrderStatus;
import com.example.shoporder.event.OrderVerifiedEvent;
import com.example.shoporder.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class OrderVerificationListener {

    private final OrderRepository orderRepository;
    private final InventoryService inventoryService;

    @RabbitListener(queues = RabbitMQConfig.ORDER_VERIFIED_QUEUE)
    @Transactional
    public void handleOrderVerified(OrderVerifiedEvent event) {
        Order order = orderRepository.findById(event.getOrderId())
                .orElseThrow(() -> new RuntimeException("Order not found"));

        if (event.isVerified()) {
            order.setStatus(OrderStatus.CONFIRMED);
        } else {
            order.setStatus(OrderStatus.REJECTED);
            // rollback stock
            for (OrderItem item : order.getItems()) {
                inventoryService.increaseStock(item.getVariantId(), item.getQuantity());
            }
        }
        orderRepository.save(order);
    }
}


package com.example.shopreview.listener;
import com.example.shopreview.entity.ReviewPermission;
import com.example.shopreview.event.OrderCreatedEvent;
import com.example.shopreview.event.OrderItemEvent;
import com.example.shopreview.repository.ReviewPermissionRepository;

import com.example.shopcore.config.RabbitMQConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Slf4j
public class OrderPaidListener {

    private final ReviewPermissionRepository permRepo;

    @Transactional
    @RabbitListener(queues = RabbitMQConfig.ORDER_PAID_QUEUE)
    public void handleOrderPaid(OrderCreatedEvent event) {
        log.info("Received OrderPaidEvent: {}", event);

        for (OrderItemEvent item : event.getItems()) {
            if (!permRepo.existsByUserIdAndVariantIdAndOrderId(event.getUserId(), item.getVariantId(), event.getOrderId())) {
                permRepo.save(ReviewPermission.builder()
                        .orderId(event.getOrderId())
                        .variantId(item.getVariantId())
                        .userId(event.getUserId())
                        .build());
            }
        }
    }
}

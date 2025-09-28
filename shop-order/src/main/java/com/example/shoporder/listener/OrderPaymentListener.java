package com.example.shoporder.listener;

import com.example.shopcore.config.RabbitMQConfig;
import com.example.shoporder.event.PaymentCompletedEvent;
import com.example.shoporder.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

// shop-order/src/main/java/com/example/shoporder/listener/OrderPaymentListener.java
@Component
@RequiredArgsConstructor
public class OrderPaymentListener {

    private final OrderService orderService;

    @RabbitListener(queues = RabbitMQConfig.PAYMENT_COMPLETED_QUEUE)
    public void handlePaymentCompleted(PaymentCompletedEvent event) {
        if (event.isSuccess()) {
            orderService.markAsPaid(event.getOrderId());
        } else {
            orderService.cancelOrderBySystem(event.getOrderId());
        }
    }
}


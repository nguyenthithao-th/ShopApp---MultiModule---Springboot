package com.example.shopproduct.listener;

import com.example.shopcore.config.RabbitMQConfig;
import com.example.shopproduct.event.OrderCreatedEvent;
import com.example.shopproduct.event.OrderItemEvent;
import com.example.shopproduct.event.OrderVerifiedEvent;
import com.example.shopproduct.repository.ProductVariantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
@RequiredArgsConstructor
public class ProductVerificationListener {

    private final ProductVariantRepository variantRepository;
    private final RabbitTemplate rabbitTemplate;

    @RabbitListener(queues = RabbitMQConfig.ORDER_CREATED_QUEUE)
    public void handleOrderCreated(OrderCreatedEvent event) {
        boolean allValid = true;

        for (OrderItemEvent item : event.getItems()) {
            var variant = variantRepository.findById(item.getVariantId()).orElse(null);
            if (variant == null) {
                allValid = false;
                break;
            }

            BigDecimal variantPrice = variant.getPrice();
            BigDecimal itemPrice = item.getPrice();

            if (variantPrice == null || itemPrice == null || variantPrice.compareTo(itemPrice) != 0) {
                allValid = false;
                break;
            }
        }

        rabbitTemplate.convertAndSend(
                RabbitMQConfig.ORDER_EXCHANGE,
                RabbitMQConfig.ORDER_VERIFIED_ROUTING_KEY,
                new OrderVerifiedEvent(event.getOrderId(), allValid)
        );
    }
}


//@Component
//@RequiredArgsConstructor
//public class ProductVerificationListener {
//
//    private final ProductVariantRepository variantRepository;
//    private final RabbitTemplate rabbitTemplate;
//
//    @RabbitListener(queues = RabbitMQConfig.ORDER_CREATED_QUEUE)
//    public void handleOrderCreated(OrderCreatedEvent event) {
//        boolean allValid = true;
//
//        for (OrderItemEvent item : event.getItems()) {
//            var variant = variantRepository.findById(item.getVariantId()).orElse(null);
//            if (variant == null || !variant.getPrice().equals(item.getPrice())) {
//                allValid = false;
//                break;
//            }
//        }
//
//        if (allValid) {
//            rabbitTemplate.convertAndSend("order.exchange", "order.verified",
//                    new OrderVerifiedEvent(event.getOrderId(), true));
//        } else {
//            rabbitTemplate.convertAndSend("order.exchange", "order.verified",
//                    new OrderVerifiedEvent(event.getOrderId(), false));
//        }
//    }
//}

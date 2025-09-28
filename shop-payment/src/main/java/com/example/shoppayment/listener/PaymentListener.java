package com.example.shoppayment.listener;

import com.example.shopcore.config.RabbitMQConfig;
import com.example.shoppayment.entity.Payment;
import com.example.shoppayment.entity.PaymentStatus;
import com.example.shoppayment.event.OrderCreatedEvent;
import com.example.shoppayment.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class PaymentListener {

    private final PaymentRepository paymentRepository;

    @Transactional
    @RabbitListener(queues = RabbitMQConfig.ORDER_CREATED_QUEUE)
    public void handleOrderCreated(OrderCreatedEvent event) {
        Payment payment = Payment.builder()
                .orderId(event.getOrderId())
                .amount(event.getTotalPrice()) // 👈 dùng totalPrice từ event
                .status(PaymentStatus.PENDING)
                .build();

        paymentRepository.save(payment);

        // Nếu cần, publish thêm PaymentCreatedEvent ở đây
    }
}

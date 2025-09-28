package com.example.shoppayment.service;

import com.example.shopcore.config.RabbitMQConfig;
import com.example.shopcore.dto.ApiResponse;
import com.example.shoppayment.dto.PaymentDto;
import com.example.shoppayment.entity.Payment;
import com.example.shoppayment.entity.PaymentStatus;
import com.example.shoppayment.event.PaymentCompletedEvent;
import com.example.shoppayment.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;

    private final RabbitTemplate rabbitTemplate;

    @Transactional
    public ApiResponse<Void> completePayment(Long paymentId, boolean success) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new RuntimeException("Payment not found"));

        payment.setStatus(success ? PaymentStatus.SUCCESS : PaymentStatus.FAILED);
        paymentRepository.save(payment);

        // Gửi event sang order
        rabbitTemplate.convertAndSend(RabbitMQConfig.SHOP_EXCHANGE, RabbitMQConfig.PAYMENT_COMPLETED_ROUTING_KEY,
                new PaymentCompletedEvent(payment.getOrderId(), success));

        return ApiResponse.ok(null);
    }


    @Transactional
    public ApiResponse<PaymentDto> createPayment(Long orderId, BigDecimal amount) {
        Payment payment = Payment.builder()
                .orderId(orderId)
                .amount(amount)
                .status(PaymentStatus.PENDING)
                .build();
        paymentRepository.save(payment);
        return ApiResponse.ok(toDto(payment));
    }

    private PaymentDto toDto(Payment p) {
        return PaymentDto.builder()
                .id(p.getId())
                .orderId(p.getOrderId())
                .amount(p.getAmount())
                .status(p.getStatus())
                .build();
    }
}
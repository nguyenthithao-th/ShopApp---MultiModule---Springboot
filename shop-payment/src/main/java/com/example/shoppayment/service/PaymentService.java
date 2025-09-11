package com.example.shoppayment.service;

import com.example.shopcore.dto.ApiResponse;
import com.example.shoppayment.dto.PaymentDto;
import com.example.shoppayment.entity.Payment;
import com.example.shoppayment.entity.PaymentStatus;
import com.example.shoppayment.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    // TODO: inject OrderServiceClient để gọi sang order (nếu muốn)

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

    @Transactional
    public ApiResponse<Void> completePayment(Long paymentId, boolean success) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new RuntimeException("Payment not found"));

        if (success) {
            payment.setStatus(PaymentStatus.SUCCESS);
            // TODO: call orderServiceClient.markAsPaid(payment.getOrderId());
        } else {
            payment.setStatus(PaymentStatus.FAILED);
            // TODO: call orderServiceClient.cancelOrder(payment.getOrderId());
        }

        paymentRepository.save(payment);
        return ApiResponse.ok(null);
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
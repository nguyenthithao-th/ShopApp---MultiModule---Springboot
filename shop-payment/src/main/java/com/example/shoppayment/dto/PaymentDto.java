package com.example.shoppayment.dto;

import com.example.shoppayment.entity.PaymentStatus;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class PaymentDto {
    private Long id;
    private Long orderId;
    private BigDecimal amount;
    private PaymentStatus status;
}

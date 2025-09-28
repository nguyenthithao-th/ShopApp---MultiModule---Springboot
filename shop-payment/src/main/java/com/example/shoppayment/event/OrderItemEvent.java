package com.example.shoppayment.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderItemEvent {
    private Long variantId;
    private int quantity;
    private BigDecimal price; // snapshot price
}

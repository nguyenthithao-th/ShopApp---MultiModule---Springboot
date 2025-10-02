package com.example.shopreview.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderCreatedEvent {
    private Long orderId;
    private List<OrderItemEvent> items;
    private Long userId;

    private BigDecimal totalPrice; // 👈 thêm cái ni vô
}


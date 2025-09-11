package com.example.shoporder.dto;

import com.example.shoporder.entity.OrderStatus;
import lombok.*;

import java.util.List;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class OrderDto {
    private Long id;
    private Long userId;
    private OrderStatus status;
    private List<OrderItemDto> items;
}
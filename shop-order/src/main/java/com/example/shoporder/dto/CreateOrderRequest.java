package com.example.shoporder.dto;

import lombok.*;

import java.util.List;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class CreateOrderRequest {
    private List<OrderItemDto> items;
}

package com.example.shoporder.dto;

import lombok.*;

import java.math.BigDecimal;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class OrderItemDto {
    private Long variantId;
    private Integer quantity;
    private BigDecimal price;
}

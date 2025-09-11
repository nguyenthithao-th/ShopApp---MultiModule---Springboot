package com.example.shoporder.dto;

import lombok.*;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class OrderItemDto {
    private Long variantId;
    private Integer quantity;
    private Double price;
}

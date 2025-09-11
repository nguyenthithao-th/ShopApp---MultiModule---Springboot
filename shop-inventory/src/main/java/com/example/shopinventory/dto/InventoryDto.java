package com.example.shopinventory.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class InventoryDto {
    private Long variantId;
    private Integer quantity;
    private Integer reserved;
}

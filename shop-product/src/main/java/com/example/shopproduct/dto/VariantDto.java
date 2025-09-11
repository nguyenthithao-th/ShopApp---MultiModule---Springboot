package com.example.shopproduct.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VariantDto {
    private Long id;
    private String sku;
    private String name;
    private BigDecimal price;
    private Map<String, String> attributes; // parse từ JSON string

    // thêm productId vào
    private Long productId;
}

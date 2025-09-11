package com.example.shopproduct.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductDto {
    private Long id;
    private String name;
    private String description;
    private Double price;
    private String imageUrl;
    private Long categoryId;

    private List<VariantDto> variants;
}



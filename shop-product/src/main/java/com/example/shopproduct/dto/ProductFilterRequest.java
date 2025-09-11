package com.example.shopproduct.dto;

import lombok.Data;

@Data
public class ProductFilterRequest {
    private String name;       // search theo tên
    private Double minPrice;   // giá tối thiểu
    private Double maxPrice;   // giá tối đa
    private String sortBy = "id"; // trường để sort (id, name, price, createdAt)
    private String sortDir = "asc"; // asc | desc
    private int page = 0;
    private int size = 10;
}


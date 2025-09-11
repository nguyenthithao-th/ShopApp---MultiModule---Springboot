package com.example.shopproduct.entity;

import com.example.shopcore.entity.BaseEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "products")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Product extends BaseEntity {

    @Column(nullable = false, length = 200)
    private String name;

    @Column(length = 1000)
    private String description;

    @Column(nullable = false)
    private Double price;

    // ảnh chính
    private String imageUrl;

    // Nhiều sản phẩm thuộc 1 category
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    @Column(nullable = false, columnDefinition = "boolean default false")
    private boolean isDeleted = false;  // soft delete flag

    // Mapping 2 chiều với variant
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonIgnore // tránh vòng lặp khi serialize entity trực tiếp
    private List<ProductVariant> variants = new ArrayList<>();
}


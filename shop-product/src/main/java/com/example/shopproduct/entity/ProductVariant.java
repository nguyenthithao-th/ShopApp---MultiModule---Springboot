package com.example.shopproduct.entity;

import com.example.shopcore.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "product_variants")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductVariant extends BaseEntity {

    @Column(nullable = false, unique = true)
    private String sku; // Mã riêng cho variant (unique)

    private String name; // VD: "Màu Đỏ - Size L"

    private BigDecimal price; // Có thể khác với giá gốc Product

    @Column(columnDefinition = "TEXT")
    private String attributes; // JSON string (vd: {"color":"red","size":"L"})

    private boolean isDeleted = false; // Soft delete

    // Quan hệ với Product
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;
}


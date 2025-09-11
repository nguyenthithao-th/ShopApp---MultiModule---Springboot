package com.example.shoporder.entity;

import com.example.shopcore.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "order_items")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class OrderItem extends BaseEntity {

    private Long variantId;   // tham chiếu sang shop-product (ProductVariant)
    private Integer quantity;
    private Double price;     // snapshot giá tại thời điểm order

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Order order;
}


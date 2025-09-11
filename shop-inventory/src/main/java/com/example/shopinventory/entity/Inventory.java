package com.example.shopinventory.entity;

import com.example.shopcore.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "inventory")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class Inventory extends BaseEntity {

    private Long variantId; // liên kết ProductVariant (ở shop-product)

    private Integer quantity; // tồn kho hiện tại

    private Integer reserved = 0; // số lượng đã giữ cho order chưa thanh toán

    public void increase(int amount) {
        this.quantity += amount;
    }

    public void decrease(int amount) {
        if (this.quantity - amount < 0) {
            throw new RuntimeException("Not enough stock");
        }
        this.quantity -= amount;
    }
}

package com.example.shoporder.entity;


import com.example.shopcore.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class Order extends BaseEntity {


    private Long userId; // tham chiếu sang shop-user

    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    // nhớ là khi tạo cái list orderitem thì phải biết thêm @Builder.Default mặc định cái này nó null nhé, ko thì lỗi
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<OrderItem> items = new ArrayList<>();
}


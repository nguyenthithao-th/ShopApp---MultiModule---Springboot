package com.example.shopreview.entity;

import com.example.shopcore.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "reviews")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class Review extends BaseEntity {

    @Column(nullable = false)
    private Long variantId; // tham chiếu sang ProductVariant

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private Integer rating; // 1–5

    @Column(columnDefinition = "TEXT")
    private String comment;

}

package com.example.shopreview.entity;

import com.example.shopcore.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "review_permissions",
        uniqueConstraints = @UniqueConstraint(columnNames = {"orderId","variantId","userId"}))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReviewPermission extends BaseEntity {
    private Long orderId;
    private Long variantId;
    private Long userId;
}

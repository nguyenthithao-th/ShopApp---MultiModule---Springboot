package com.example.shopreview.repository;


import com.example.shopreview.entity.ReviewPermission;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewPermissionRepository extends JpaRepository<ReviewPermission, Long> {
    boolean existsByUserIdAndVariantIdAndOrderId(Long userId, Long variantId, Long orderId);
}


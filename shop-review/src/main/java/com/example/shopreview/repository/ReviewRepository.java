package com.example.shopreview.repository;


import com.example.shopreview.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findByVariantIdOrderByCreatedAtDesc(Long variantId);
}


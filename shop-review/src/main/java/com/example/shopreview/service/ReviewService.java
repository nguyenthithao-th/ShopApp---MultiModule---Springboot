package com.example.shopreview.service;

import com.example.shopcore.dto.ApiResponse;
import com.example.shopreview.dto.CreateReviewRequest;
import com.example.shopreview.dto.ReviewDto;
import com.example.shopreview.entity.Review;
import com.example.shopreview.repository.ReviewPermissionRepository;
import com.example.shopreview.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepo;
    private final ReviewPermissionRepository permRepo;

    @Transactional
    public ApiResponse<ReviewDto> createReview(Long userId, CreateReviewRequest req) {
        if (!permRepo.existsByUserIdAndVariantIdAndOrderId(userId, req.getVariantId(), req.getOrderId())) {
            throw new RuntimeException("You cannot review this item (not paid or not purchased)");
        }

        if (req.getRating() < 1 || req.getRating() > 5) {
            throw new RuntimeException("Rating must be between 1 and 5");
        }

        Review review = Review.builder()
                .variantId(req.getVariantId())
                .userId(userId)
                .rating(req.getRating())
                .comment(req.getComment())
                .build();

        Review saved = reviewRepo.save(review);

        return ApiResponse.ok(
                ReviewDto.builder()
                        .id(saved.getId())
                        .variantId(saved.getVariantId())
                        .userId(saved.getUserId())
                        .rating(saved.getRating())
                        .comment(saved.getComment())
                        .createdAt(saved.getCreatedAt())
                        .build()
        );
    }

    @Transactional(readOnly = true)
    public ApiResponse<List<ReviewDto>> getReviewsByVariant(Long variantId) {
        List<ReviewDto> list = reviewRepo.findByVariantIdOrderByCreatedAtDesc(variantId)
                .stream()
                .map(r -> ReviewDto.builder()
                        .id(r.getId())
                        .variantId(r.getVariantId())
                        .userId(r.getUserId())
                        .rating(r.getRating())
                        .comment(r.getComment())
                        .createdAt(r.getCreatedAt())
                        .build())
                .collect(Collectors.toList());

        return ApiResponse.ok(list);
    }
}

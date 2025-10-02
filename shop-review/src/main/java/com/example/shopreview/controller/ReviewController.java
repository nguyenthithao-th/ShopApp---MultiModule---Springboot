package com.example.shopreview.controller;

import com.example.shopauth.service.CustomUserDetails;
import com.example.shopcore.dto.ApiResponse;
import com.example.shopreview.dto.CreateReviewRequest;
import com.example.shopreview.dto.ReviewDto;
import com.example.shopreview.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping
    public ApiResponse<ReviewDto> createReview(@RequestBody CreateReviewRequest req,
                                               @AuthenticationPrincipal CustomUserDetails principal) {
        return reviewService.createReview(principal.getId(), req);
    }

    @GetMapping("/variant/{variantId}")
    public ApiResponse<List<ReviewDto>> getReviewsByVariant(@PathVariable("variantId") Long variantId) {
        return reviewService.getReviewsByVariant(variantId);
    }
}

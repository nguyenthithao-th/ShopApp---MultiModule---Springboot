package com.example.shopreview.dto;

import lombok.*;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class CreateReviewRequest {
    private Long variantId;
    private Long orderId;
    private Integer rating;
    private String comment;
}

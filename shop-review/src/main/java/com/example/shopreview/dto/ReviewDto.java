package com.example.shopreview.dto;

import lombok.*;
import java.time.Instant;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class ReviewDto {
    private Long id;
    private Long variantId;
    private Long userId;
    private Integer rating;
    private String comment;
    private Instant createdAt;
}

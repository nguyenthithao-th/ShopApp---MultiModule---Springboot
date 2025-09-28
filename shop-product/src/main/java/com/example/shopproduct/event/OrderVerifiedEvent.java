package com.example.shopproduct.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderVerifiedEvent {
    private Long orderId;
    private boolean verified;
}

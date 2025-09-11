package com.example.shoporder.controller;

import com.example.shopauth.service.CustomUserDetails;
import com.example.shopcore.dto.ApiResponse;
import com.example.shoporder.dto.CreateOrderRequest;
import com.example.shoporder.dto.OrderDto;
import com.example.shoporder.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;


    @PostMapping
    @PreAuthorize("hasRole('USER')")
    public ApiResponse<OrderDto> createOrder(
            @RequestBody CreateOrderRequest req,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        Long userId = userDetails.getId(); // lấy userId từ token
        return orderService.createOrder(req, userId);
    }


    @PostMapping("/{orderId}/cancel")
    @PreAuthorize("hasRole('USER')")
    public ApiResponse<Void> cancelOrder(@PathVariable("orderId") Long orderId,
                                         @AuthenticationPrincipal CustomUserDetails principal) {
        return orderService.cancelOrder(orderId, principal.getId(), principal.getAuthorities());
    }

}

package com.example.shoppayment.controller;

import com.example.shopcore.dto.ApiResponse;
import com.example.shoppayment.dto.PaymentDto;
import com.example.shoppayment.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping
    @PreAuthorize("hasRole('USER')")
    public ApiResponse<PaymentDto> create(@RequestParam(name = "orderId") Long orderId,
                                          @RequestParam(name = "amount") BigDecimal amount) {
        return paymentService.createPayment(orderId, amount);
    }

    @PostMapping("/{paymentId}/complete")
    @PreAuthorize("hasRole('USER')")
    public ApiResponse<Void> complete(@PathVariable("paymentId") Long paymentId,
                                      @RequestParam(name = "success") boolean success) {
        return paymentService.completePayment(paymentId, success);
    }
}
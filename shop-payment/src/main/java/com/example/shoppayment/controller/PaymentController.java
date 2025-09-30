package com.example.shoppayment.controller;

import com.example.shopcore.dto.ApiResponse;
import com.example.shoppayment.dto.PaymentDto;
import com.example.shoppayment.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    /**
     * FE gọi để hoàn tất payment sau khi user thanh toán xong
     */
    @PostMapping("/{paymentId}/complete")
    @PreAuthorize("hasRole('USER')")
    public ApiResponse<Void> complete(@PathVariable("paymentId") Long paymentId,
                                      @RequestParam(name = "success") boolean success) {
        return paymentService.completePayment(paymentId, success);
    }

    /**
     * Cho phép FE query payment detail
     */
    @GetMapping("/{paymentId}")
    @PreAuthorize("hasRole('USER')")
    public ApiResponse<PaymentDto> getPayment(@PathVariable("paymentId") Long paymentId) {
        return paymentService.getPayment(paymentId);
    }
}

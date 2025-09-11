package com.example.shopproduct.controller;

import com.example.shopcore.dto.ApiResponse;
import com.example.shopproduct.dto.VariantDto;
import com.example.shopproduct.service.ProductVariantService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products/{productId}/variants")
@RequiredArgsConstructor
public class ProductVariantController {

    private final ProductVariantService variantService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<VariantDto> createVariant(
            @PathVariable("productId") Long productId,
            @RequestBody VariantDto dto) {
        return variantService.createVariant(productId, dto);
    }

    @GetMapping
    public ApiResponse<List<VariantDto>> getVariants(@PathVariable("productId") Long productId) {
        return variantService.getVariantsByProduct(productId);
    }

    @DeleteMapping("/{variantId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<Void> deleteVariant(@PathVariable("variantId") Long variantId) {
        return variantService.softDelete(variantId);
    }

    @PutMapping("/{variantId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<VariantDto> updateVariant(@PathVariable("productId") Long productId,
                                                 @PathVariable("variantId") Long variantId,
                                                 @RequestBody VariantDto dto) {
        return variantService.updateVariant(productId, variantId, dto);
    }

    @DeleteMapping("/{variantId}/hard")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<Void> hardDelete(@PathVariable("variantId") Long variantId) {
        return variantService.hardDelete(variantId);
    }

    @GetMapping("/{variantId}")
    public ApiResponse<VariantDto> getVariantById(@PathVariable("productId") Long productId,
                                                  @PathVariable("variantId") Long variantId) {
        return variantService.getVariantById(productId, variantId);
    }

}


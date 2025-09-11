package com.example.shopproduct.controller;

import com.example.shopcore.dto.ApiResponse;
import com.example.shopproduct.dto.ProductDto;
import com.example.shopproduct.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/recycle-bin/products")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class RecycleBinController {

    private final ProductService productService;

    // Lấy danh sách sản phẩm đã xoá (recycle bin)
    @GetMapping
    public ApiResponse<List<ProductDto>> getDeletedProducts() {
        return productService.getDeletedProducts();
    }

    // Soft delete (chuyển vào recycle bin)
    @DeleteMapping("/{id}")
    public ApiResponse<Void> softDelete(@PathVariable("id") Long id) {
        return productService.softDeleteProduct(id);
    }

    // Restore từ recycle bin
    @PutMapping("/{id}/restore")
    public ApiResponse<Void> restore(@PathVariable("id") Long id) {
        return productService.restoreProduct(id);
    }

    // Hard delete (xoá vĩnh viễn)
    @DeleteMapping("/{id}/hard")
    public ApiResponse<Void> hardDelete(@PathVariable("id") Long id) {
        return productService.hardDeleteProduct(id);
    }
}


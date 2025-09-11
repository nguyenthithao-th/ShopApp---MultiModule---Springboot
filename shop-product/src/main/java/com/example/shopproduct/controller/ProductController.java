package com.example.shopproduct.controller;

import com.example.shopcore.dto.ApiResponse;
import com.example.shopproduct.dto.PageResponse;
import com.example.shopproduct.dto.ProductDto;
import com.example.shopproduct.dto.ProductFilterRequest;
import com.example.shopproduct.service.ProductService;
import com.example.shopproduct.storage.FileStorageService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;
    private final FileStorageService fileStorageService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<ProductDto> create(@RequestBody @Valid ProductDto dto) {
        return productService.createProduct(dto);
    }

    @GetMapping
    public ApiResponse<List<ProductDto>> getAll() {
        return productService.getAll();
    }

    @GetMapping("/category/{categoryId}")
    public ApiResponse<List<ProductDto>> getByCategory(@PathVariable Long categoryId) {
        return productService.getByCategory(categoryId);
    }


    @PostMapping("/upload")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<String> upload(@RequestParam("file") MultipartFile file) {
        try {
            String url = fileStorageService.save(file);
            return ApiResponse.ok(url);
        } catch (IOException e) {
            return ApiResponse.error("Upload failed: " + e.getMessage());
        }
    }

    @PostMapping("/upload-multiple")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<List<String>> uploadMultiple(@RequestParam("files") List<MultipartFile> files) {
        try {
            List<String> urls = files.stream()
                    .map(f -> {
                        try {
                            return fileStorageService.save(f);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    })
                    .toList();
            return ApiResponse.ok(urls);
        } catch (Exception e) {
            return ApiResponse.error("Upload failed: " + e.getMessage());
        }
    }

    @PostMapping("/search")
    public ApiResponse<PageResponse<ProductDto>> search(@RequestBody ProductFilterRequest filter) {
        return productService.search(filter);
    }


}


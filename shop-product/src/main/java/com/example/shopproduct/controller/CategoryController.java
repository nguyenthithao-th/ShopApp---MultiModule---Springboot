package com.example.shopproduct.controller;

import com.example.shopcore.dto.ApiResponse;
import com.example.shopproduct.dto.CategoryDto;
import com.example.shopproduct.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<CategoryDto> create(@RequestBody CategoryDto dto) {
        return categoryService.createCategory(dto);
    }

    @GetMapping
    public ApiResponse<List<CategoryDto>> getAll() {
        return categoryService.getAllCategories();
    }
}


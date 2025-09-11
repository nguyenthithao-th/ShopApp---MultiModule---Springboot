package com.example.shopproduct.service;

import com.example.shopcore.dto.ApiResponse;
import com.example.shopproduct.dto.CategoryDto;
import com.example.shopproduct.dto.ProductDto;
import com.example.shopproduct.entity.Category;
import com.example.shopproduct.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public ApiResponse<List<CategoryDto>> getAllCategories() {
        List<CategoryDto> list = categoryRepository.findAll().stream()
                .map(this::mapToCategoryDto)
                .toList();
        return ApiResponse.ok(list);
    }

    public ApiResponse<CategoryDto> createCategory(CategoryDto dto) {
        Category category = Category.builder()
                .name(dto.getName())
                .build();

        Category saved = categoryRepository.save(category);
        return ApiResponse.ok(mapToCategoryDto(saved));
    }

    private CategoryDto mapToCategoryDto(Category c) {
        List<ProductDto> products = c.getProducts().stream()
                .map(p -> ProductDto.builder()
                        .id(p.getId())
                        .name(p.getName())
                        .description(p.getDescription())
                        .price(p.getPrice())
                        .imageUrl(p.getImageUrl())
                        .categoryId(c.getId())
                        .build()
                ).toList();

        return CategoryDto.builder()
                .id(c.getId())
                .name(c.getName())
                .products(products)
                .build();
    }
}


package com.example.shopproduct.service;

import com.example.shopcore.dto.ApiResponse;
import com.example.shopcore.exception.BusinessException;
import com.example.shopproduct.dto.PageResponse;
import com.example.shopproduct.dto.ProductDto;
import com.example.shopproduct.dto.ProductFilterRequest;
import com.example.shopproduct.dto.VariantDto;
import com.example.shopproduct.entity.Category;
import com.example.shopproduct.entity.Product;
import com.example.shopproduct.entity.ProductVariant;
import com.example.shopproduct.filter.ProductSpecification;
import com.example.shopproduct.repository.CategoryRepository;
import com.example.shopproduct.repository.ProductRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ObjectMapper objectMapper;

    public ApiResponse<ProductDto> createProduct(ProductDto dto) {
        Category category = categoryRepository.findById(dto.getCategoryId())
                .orElseThrow(() -> new BusinessException("Category not found"));

        Product product = Product.builder()
                .name(dto.getName())
                .description(dto.getDescription())
                .price(dto.getPrice())
                .imageUrl(dto.getImageUrl())
                .category(category)
                .build();

        Product saved = productRepository.save(product);

        return ApiResponse.ok(toDto(saved));
    }

    public ApiResponse<List<ProductDto>> getAll() {
        return ApiResponse.ok(
                productRepository.findAllByIsDeletedFalse()
                        .stream()
                        .map(this::toDto)
                        .toList()
        );
    }

    public ApiResponse<List<ProductDto>> getByCategory(Long categoryId) {
        return ApiResponse.ok(
                productRepository.findByCategoryIdAndIsDeletedFalse(categoryId)
                        .stream()
                        .map(this::toDto)
                        .toList()
        );
    }

    private ProductDto toDto(Product p) {
        ProductDto dto = new ProductDto();
        dto.setId(p.getId());
        dto.setName(p.getName());
        dto.setDescription(p.getDescription());
        dto.setPrice(p.getPrice());
        dto.setImageUrl(p.getImageUrl());
        dto.setCategoryId(p.getCategory() != null ? p.getCategory().getId() : null);

        // map variants nếu có
        if (p.getVariants() != null) {
            dto.setVariants(
                    p.getVariants().stream().map(this::variantToDto).toList()
            );
        }

        return dto;
    }

    private VariantDto variantToDto(ProductVariant v) {
        VariantDto dto = new VariantDto();
        dto.setId(v.getId());
        dto.setSku(v.getSku());
        dto.setName(v.getName());
        dto.setPrice(v.getPrice());

        try {
            if (v.getAttributes() != null) {
                ObjectMapper mapper = new ObjectMapper();
                dto.setAttributes(
                        mapper.readValue(v.getAttributes(), new TypeReference<Map<String, String>>() {})
                );
            }
        } catch (Exception e) {
            dto.setAttributes(Map.of());
        }

        return dto;
    }



//    public ApiResponse<Page<ProductDto>> search(ProductFilterRequest filter) {
//        Sort sort = filter.getSortDir().equalsIgnoreCase("desc")
//                ? Sort.by(filter.getSortBy()).descending()
//                : Sort.by(filter.getSortBy()).ascending();
//
//        Pageable pageable = PageRequest.of(filter.getPage(), filter.getSize(), sort);
//
//        Specification<Product> spec = (root, query, cb) -> cb.conjunction();
//
//        if (filter.getName() != null && !filter.getName().isEmpty()) {
//            spec = spec.and(ProductSpecification.hasNameLike(filter.getName()));
//        }
//        if (filter.getMinPrice() != null) {
//            spec = spec.and(ProductSpecification.priceGreaterThanOrEq(filter.getMinPrice()));
//        }
//        if (filter.getMaxPrice() != null) {
//            spec = spec.and(ProductSpecification.priceLessThanOrEq(filter.getMaxPrice()));
//        }
//
//        Page<ProductDto> result = productRepository.findAll(spec, pageable)
//                .map(this::toDto);
//
//        return ApiResponse.ok(result);
//    }

//    public ApiResponse<PageResponse<ProductDto>> search(ProductFilterRequest filter) {
//        Sort sort = filter.getSortDir().equalsIgnoreCase("desc")
//                ? Sort.by(filter.getSortBy()).descending()
//                : Sort.by(filter.getSortBy()).ascending();
//
//        Pageable pageable = PageRequest.of(filter.getPage(), filter.getSize(), sort);
//
//        Specification<Product> spec = (root, query, cb) -> cb.conjunction();
//        if (filter.getName() != null && !filter.getName().isEmpty()) {
//            spec = spec.and(ProductSpecification.hasNameLike(filter.getName()));
//        }
//        if (filter.getMinPrice() != null) {
//            spec = spec.and(ProductSpecification.priceGreaterThanOrEq(filter.getMinPrice()));
//        }
//        if (filter.getMaxPrice() != null) {
//            spec = spec.and(ProductSpecification.priceLessThanOrEq(filter.getMaxPrice()));
//        }
//
//        Page<ProductDto> result = productRepository.findAll(spec, pageable)
//                .map(this::toDto);
//
//        PageResponse<ProductDto> response = new PageResponse<>(
//                result.getContent(),
//                result.getNumber(),
//                result.getSize(),
//                result.getTotalElements(),
//                result.getTotalPages(),
//                result.isLast()
//        );
//
//        return ApiResponse.ok(response);
//    }

    public ApiResponse<PageResponse<ProductDto>> search(ProductFilterRequest filter) {
        Sort sort = filter.getSortDir().equalsIgnoreCase("desc")
                ? Sort.by(filter.getSortBy()).descending()
                : Sort.by(filter.getSortBy()).ascending();

        Pageable pageable = PageRequest.of(filter.getPage(), filter.getSize(), sort);

        Specification<Product> spec = (root, query, cb) -> cb.conjunction();
        if (filter.getName() != null && !filter.getName().isEmpty()) {
            spec = spec.and(ProductSpecification.hasNameLike(filter.getName()));
        }
        if (filter.getMinPrice() != null) {
            spec = spec.and(ProductSpecification.priceGreaterThanOrEq(filter.getMinPrice()));
        }
        if (filter.getMaxPrice() != null) {
            spec = spec.and(ProductSpecification.priceLessThanOrEq(filter.getMaxPrice()));
        }

        Page<ProductDto> result = productRepository.findAllByIsDeletedFalse(spec, pageable)
                .map(this::toDto);

        PageResponse<ProductDto> response = new PageResponse<>(
                result.getContent(),
                result.getNumber(),
                result.getSize(),
                result.getTotalElements(),
                result.getTotalPages(),
                result.isLast()
        );

        return ApiResponse.ok(response);
    }


    // Soft delete
    public ApiResponse<Void> softDeleteProduct(Long id) {
        Product p = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        p.setDeleted(true);
        productRepository.save(p);
        return ApiResponse.ok(null);
    }


    public ApiResponse<Void> restoreProduct(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        if (!product.isDeleted()) {
            return ApiResponse.error("Product is not deleted");
        }
        product.setDeleted(false);
        productRepository.save(product);
        return ApiResponse.ok(null);
    }


    public ApiResponse<Void> hardDeleteProduct(Long id) {
        Product p = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        if (!p.isDeleted()) {
            throw new RuntimeException("Product must be soft deleted before hard delete");
        }

        productRepository.delete(p);
        return ApiResponse.ok(null);
    }


    public ApiResponse<List<ProductDto>> getDeletedProducts() {
        List<ProductDto> deleted = productRepository.findAll().stream()
                .filter(Product::isDeleted)
                .map(this::toDto)
                .toList();
        return ApiResponse.ok(deleted);
    }

}
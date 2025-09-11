package com.example.shopproduct.service;

import com.example.shopcore.dto.ApiResponse;
import com.example.shopproduct.dto.VariantDto;
import com.example.shopproduct.entity.Product;
import com.example.shopproduct.entity.ProductVariant;
import com.example.shopproduct.repository.ProductRepository;
import com.example.shopproduct.repository.ProductVariantRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ProductVariantService {

    private final ProductRepository productRepository;
    private final ProductVariantRepository variantRepository;
    private final ObjectMapper mapper;

    public ApiResponse<VariantDto> createVariant(Long productId, VariantDto dto) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        String attributesJson = null;
        try {
            if (dto.getAttributes() != null) {
                attributesJson = mapper.writeValueAsString(dto.getAttributes());
            }
        } catch (Exception e) {
            throw new RuntimeException("Invalid attributes JSON");
        }

        ProductVariant variant = ProductVariant.builder()
                .sku(dto.getSku())
                .name(dto.getName())
                .price(dto.getPrice())
                .attributes(attributesJson)
                .product(product)
                .build();

        variantRepository.save(variant);

        return ApiResponse.ok(toDto(variant));
    }

    public ApiResponse<List<VariantDto>> getVariantsByProduct(Long productId) {
        List<VariantDto> variants = variantRepository.findByProductIdAndIsDeletedFalse(productId)
                .stream()
                .map(this::toDto)
                .toList();
        return ApiResponse.ok(variants);
    }

    public ApiResponse<Void> softDelete(Long id) {
        ProductVariant v = variantRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Variant not found"));
        v.setDeleted(true);
        variantRepository.save(v);
        return ApiResponse.ok(null);
    }

    private VariantDto toDto(ProductVariant v) {
        try {
            return VariantDto.builder()
                    .id(v.getId())
                    .sku(v.getSku())
                    .name(v.getName())
                    .price(v.getPrice())
                    .attributes(v.getAttributes() != null
                            ? mapper.readValue(v.getAttributes(), new TypeReference<Map<String, String>>() {})
                            : Map.of())
                    .productId(v.getProduct().getId())
                    .build();
        } catch (Exception e) {
            return VariantDto.builder()
                    .id(v.getId())
                    .sku(v.getSku())
                    .name(v.getName())
                    .price(v.getPrice())
                    .attributes(Map.of())
                    .productId(v.getProduct().getId())
                    .build();
        }
    }

    // them updateVariant,và hardDelete

    public ApiResponse<VariantDto> updateVariant(Long productId, Long variantId, VariantDto dto) {
        ProductVariant variant = variantRepository.findById(variantId)
                .orElseThrow(() -> new RuntimeException("Variant not found"));

        if (!variant.getProduct().getId().equals(productId)) {
            throw new RuntimeException("Variant does not belong to this product");
        }

        variant.setSku(dto.getSku());
        variant.setName(dto.getName());
        variant.setPrice(dto.getPrice());

        try {
            if (dto.getAttributes() != null) {
                variant.setAttributes(mapper.writeValueAsString(dto.getAttributes()));
            }
        } catch (Exception e) {
            throw new RuntimeException("Invalid attributes JSON");
        }

        variantRepository.save(variant);
        return ApiResponse.ok(toDto(variant));
    }


    public ApiResponse<Void> hardDelete(Long variantId) {
        ProductVariant variant = variantRepository.findById(variantId)
                .orElseThrow(() -> new RuntimeException("Variant not found"));

        variantRepository.delete(variant);
        return ApiResponse.ok(null);
    }

    public ApiResponse<VariantDto> getVariantById(Long productId, Long variantId) {
        ProductVariant variant = variantRepository.findById(variantId)
                .orElseThrow(() -> new RuntimeException("Variant not found"));

        if (!variant.getProduct().getId().equals(productId)) {
            throw new RuntimeException("Variant does not belong to this product");
        }

        return ApiResponse.ok(toDto(variant));
    }


}


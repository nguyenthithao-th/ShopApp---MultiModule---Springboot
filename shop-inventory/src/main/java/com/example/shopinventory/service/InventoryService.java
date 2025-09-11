package com.example.shopinventory.service;

import com.example.shopcore.dto.ApiResponse;
import com.example.shopinventory.dto.InventoryDto;
import com.example.shopinventory.entity.Inventory;
import com.example.shopinventory.repository.InventoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class InventoryService {

    private final InventoryRepository inventoryRepository;

    public ApiResponse<InventoryDto> getByVariant(Long variantId) {
        Inventory inv = inventoryRepository.findByVariantId(variantId)
                .orElseThrow(() -> new RuntimeException("No inventory found for variant " + variantId));

        return ApiResponse.ok(toDto(inv));
    }

    @Transactional
    public ApiResponse<InventoryDto> increaseStock(Long variantId, int amount) {
        Inventory inv = inventoryRepository.findByVariantId(variantId)
                .orElseGet(() -> Inventory.builder()
                        .variantId(variantId)
                        .quantity(0)
                        .reserved(0)
                        .build());

        inv.increase(amount);
        inventoryRepository.save(inv);

        return ApiResponse.ok(toDto(inv));
    }

    @Transactional
    public ApiResponse<InventoryDto> decreaseStock(Long variantId, int amount) {
        Inventory inv = inventoryRepository.findByVariantId(variantId)
                .orElseThrow(() -> new RuntimeException("No inventory found for variant " + variantId));

        inv.decrease(amount);
        inventoryRepository.save(inv);

        return ApiResponse.ok(toDto(inv));
    }

    private InventoryDto toDto(Inventory inv) {
        return InventoryDto.builder()
                .variantId(inv.getVariantId())
                .quantity(inv.getQuantity())
                .reserved(inv.getReserved())
                .build();
    }
}

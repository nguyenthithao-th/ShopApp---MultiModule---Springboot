package com.example.shopinventory.controller;

import com.example.shopcore.dto.ApiResponse;
import com.example.shopinventory.dto.InventoryDto;
import com.example.shopinventory.service.InventoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/inventory")
@RequiredArgsConstructor
public class InventoryController {

    private final InventoryService inventoryService;

    @GetMapping("/{variantId}")
    public ApiResponse<InventoryDto> getByVariant(@PathVariable("variantId") Long variantId) {
        return inventoryService.getByVariant(variantId);
    }

    @PostMapping("/{variantId}/increase")
    public ApiResponse<InventoryDto> increaseStock(@PathVariable("variantId") Long variantId,
                                                   @RequestParam(name = "amount") int amount) {
        return inventoryService.increaseStock(variantId, amount);
    }

    @PostMapping("/{variantId}/decrease")
    public ApiResponse<InventoryDto> decreaseStock(@PathVariable("variantId") Long variantId,
                                                   @RequestParam(name = "amount") int amount) {
        return inventoryService.decreaseStock(variantId, amount);
    }
}

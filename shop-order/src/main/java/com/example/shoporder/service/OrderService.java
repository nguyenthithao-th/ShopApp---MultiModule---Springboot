package com.example.shoporder.service;

import com.example.shopcore.dto.ApiResponse;
import com.example.shopinventory.service.InventoryService;
import com.example.shoporder.dto.*;
import com.example.shoporder.entity.*;
import com.example.shoporder.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final InventoryService inventoryService; // từ shop-inventory

    // Luu y quan trong, khi inject module khac thi nho them scanBasePackages

    @Transactional
    public ApiResponse<OrderDto> createOrder(CreateOrderRequest req, Long userId) {
        Order order = Order.builder()
                .userId(userId)
                .status(OrderStatus.PENDING)
                .build();

        for (OrderItemDto itemDto : req.getItems()) {
            // gọi inventory để giảm stock
            inventoryService.decreaseStock(itemDto.getVariantId(), itemDto.getQuantity());

            OrderItem item = OrderItem.builder()
                    .variantId(itemDto.getVariantId())
                    .quantity(itemDto.getQuantity())
                    .price(itemDto.getPrice())
                    .order(order)
                    .build();

            order.getItems().add(item);
        }

        orderRepository.save(order);

        return ApiResponse.ok(toDto(order));
    }

//    @Transactional
//    public ApiResponse<Void> cancelOrder(Long orderId) {
//        Order order = orderRepository.findById(orderId)
//                .orElseThrow(() -> new RuntimeException("Order not found"));
//
//        if (order.getStatus() == OrderStatus.CANCELLED) {
//            throw new RuntimeException("Order already cancelled");
//        }
//
//        order.setStatus(OrderStatus.CANCELLED);
//
//        // hoàn lại stock
//        for (OrderItem item : order.getItems()) {
//            inventoryService.increaseStock(item.getVariantId(), item.getQuantity());
//        }
//
//        orderRepository.save(order);
//        return ApiResponse.ok(null);
//    }

    @Transactional
    public ApiResponse<Void> cancelOrder(Long orderId, Long userId, Collection<? extends GrantedAuthority> authorities) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        if (order.getStatus() == OrderStatus.CANCELLED) {
            throw new RuntimeException("Order already cancelled");
        }

        // 👇 Kiểm tra quyền: owner hoặc ADMIN
        boolean isAdmin = authorities.stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        if (!isAdmin && !order.getUserId().equals(userId)) {
            throw new RuntimeException("You are not allowed to cancel this order");
        }

        order.setStatus(OrderStatus.CANCELLED);

        // hoàn lại stock
        for (OrderItem item : order.getItems()) {
            inventoryService.increaseStock(item.getVariantId(), item.getQuantity());
        }

        orderRepository.save(order);
        return ApiResponse.ok(null);
    }


    private OrderDto toDto(Order order) {
        return OrderDto.builder()
                .id(order.getId())
                .userId(order.getUserId())
                .status(order.getStatus())
                .items(order.getItems().stream().map(i ->
                        OrderItemDto.builder()
                                .variantId(i.getVariantId())
                                .quantity(i.getQuantity())
                                .price(i.getPrice())
                                .build()
                ).collect(Collectors.toList()))
                .build();
    }
}

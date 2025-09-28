package com.example.shoporder.service;

import com.example.shopcore.config.RabbitMQConfig;
import com.example.shopcore.dto.ApiResponse;
import com.example.shopinventory.service.InventoryService;
import com.example.shoporder.dto.*;
import com.example.shoporder.entity.*;
import com.example.shoporder.event.OrderCreatedEvent;
import com.example.shoporder.event.OrderItemEvent;
import com.example.shoporder.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final InventoryService inventoryService; // từ shop-inventory
    private final RabbitTemplate rabbitTemplate;

    @Transactional
    public ApiResponse<OrderDto> createOrder(CreateOrderRequest req, Long userId) {
        Order order = Order.builder()
                .userId(userId)
                .status(OrderStatus.PENDING_VERIFICATION)
                .build();

        List<OrderItemEvent> itemEvents = new ArrayList<>();

        for (OrderItemDto itemDto : req.getItems()) {
            // giảm stock tạm thời
            inventoryService.decreaseStock(itemDto.getVariantId(), itemDto.getQuantity());

            OrderItem item = OrderItem.builder()
                    .variantId(itemDto.getVariantId())
                    .quantity(itemDto.getQuantity())
                    .price(itemDto.getPrice()) // client gửi, snapshot
                    .order(order)
                    .build();

            order.getItems().add(item);

            itemEvents.add(
                    OrderItemEvent.builder()
                            .variantId(itemDto.getVariantId())
                            .quantity(itemDto.getQuantity())
                            .price(itemDto.getPrice())
                            .build()
            );
        }

        orderRepository.save(order);

        // publish event
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.ORDER_EXCHANGE,
                RabbitMQConfig.ORDER_CREATED_ROUTING_KEY,
                new OrderCreatedEvent(order.getId(), itemEvents, userId)
        );

        return ApiResponse.ok(toDto(order));
    }

    @Transactional
    public void markAsPaid(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        order.setStatus(OrderStatus.PAID);
        orderRepository.save(order);
    }


    @Transactional
    public ApiResponse<Void> cancelOrderBySystem(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        if (order.getStatus() == OrderStatus.CANCELLED) {
            return ApiResponse.ok(null);
        }

        order.setStatus(OrderStatus.CANCELLED);

        // hoàn lại stock
        for (OrderItem item : order.getItems()) {
            inventoryService.increaseStock(item.getVariantId(), item.getQuantity());
        }

        orderRepository.save(order);
        return ApiResponse.ok(null);
    }


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

package com.leobarrosl.mercado_simples.models.dtos.reponses;

import com.leobarrosl.mercado_simples.models.entities.Order;

import java.time.LocalDateTime;
import java.util.List;

public record OrderResponseDTO(Long id, String customerName, List<OrderItemResponseDTO> items, Double totalPrice,
                               LocalDateTime createdAt, String status) {

    public static OrderResponseDTO toDto(Order order) {
        return new OrderResponseDTO(
            order.getId(),
            order.getCustomer().getName(),
            order.getItems().stream().map(OrderItemResponseDTO::toDto).toList(),
            order.getTotalPrice(),
            order.getOrderDate(),
            order.getStatus().name()
        );
    }
}

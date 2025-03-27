package com.leobarrosl.mercado_simples.models.dtos.reponses;

import com.leobarrosl.mercado_simples.models.entities.OrderItem;

public record OrderItemResponseDTO(Long id, String productName, Integer quantity, Double salePrice) {

    public static OrderItemResponseDTO toDto(OrderItem orderItem) {
        return new OrderItemResponseDTO(
            orderItem.getId(),
            orderItem.getItem().getName(),
            orderItem.getQuantity(),
            orderItem.getSalePrice()
        );
    }
}

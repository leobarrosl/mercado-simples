package com.leobarrosl.mercado_simples.models.dtos;

import lombok.Data;

@Data
public class OrderItemDTO {
    private Long productId;
    private Integer quantity;
    // O preço será obtido do produto no momento da criação
}
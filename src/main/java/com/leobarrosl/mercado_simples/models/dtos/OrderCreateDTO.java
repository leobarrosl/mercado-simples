package com.leobarrosl.mercado_simples.models.dtos;

import java.util.List;

import lombok.Data;

@Data
public class OrderCreateDTO {
    private Long customerId;
    private List<OrderItemDTO> items;
}
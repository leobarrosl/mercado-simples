package com.leobarrosl.mercado_simples.controllers;

import java.util.List;

import com.leobarrosl.mercado_simples.models.dtos.reponses.OrderResponseDTO;
import com.leobarrosl.mercado_simples.models.dtos.requests.OrderItemDTO;
import com.leobarrosl.mercado_simples.models.enums.OrderStatusEnum;
import jakarta.websocket.server.PathParam;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import com.leobarrosl.mercado_simples.models.dtos.requests.OrderCreateDTO;
import com.leobarrosl.mercado_simples.models.entities.Order;
import com.leobarrosl.mercado_simples.services.OrderService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/orders")
public class OrderController {

    private final OrderService orderService;

    @GetMapping
    public List<OrderResponseDTO> findAll() {
        return orderService.findAll().stream().map(OrderResponseDTO::toDto).toList();
    }

    @GetMapping("/page")
    public Page<OrderResponseDTO> findAllPageable(Pageable pageable) {
        return orderService.findAllPageable(pageable).map(OrderResponseDTO::toDto);
    }

    @GetMapping("/{id}")
    public OrderResponseDTO findById(@PathVariable Long id) {
        return OrderResponseDTO.toDto(orderService.findById(id));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public OrderResponseDTO createOrder(@RequestBody OrderCreateDTO orderDTO) {
        return OrderResponseDTO.toDto(orderService.createOrder(orderDTO));
    }

    @PostMapping("/{orderId}/items")
    public OrderResponseDTO addItemToOrder(@PathVariable Long orderId, @RequestBody OrderItemDTO itemDTO) {
        return OrderResponseDTO.toDto(orderService.addItemToOrder(orderId, itemDTO));
    }

    @DeleteMapping("/{orderId}/items/{itemId}")
    public OrderResponseDTO removeItemFromOrder(@PathVariable Long orderId, @PathVariable Long itemId) {
        return OrderResponseDTO.toDto(orderService.removeItemFromOrder(orderId, itemId));
    }

    @PatchMapping("{id}")
    public OrderResponseDTO changeStatus(@PathVariable Long id, @PathParam("newStatus") OrderStatusEnum newStatus) {
        Order order = orderService.findById(id);
        order.setStatus(newStatus);
        return OrderResponseDTO.toDto(orderService.save(order));
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        orderService.delete(id);
    }

}

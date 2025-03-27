package com.leobarrosl.mercado_simples.services;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.leobarrosl.mercado_simples.exceptions.NotFoundException;
import com.leobarrosl.mercado_simples.models.dtos.requests.OrderCreateDTO;
import com.leobarrosl.mercado_simples.models.dtos.requests.OrderItemDTO;
import com.leobarrosl.mercado_simples.models.entities.Customer;
import com.leobarrosl.mercado_simples.models.entities.Item;
import com.leobarrosl.mercado_simples.models.entities.Order;
import com.leobarrosl.mercado_simples.models.entities.OrderItem;
import com.leobarrosl.mercado_simples.models.enums.OrderStatusEnum;
import com.leobarrosl.mercado_simples.repositories.OrderRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final CustomerService customerService;
    private final ItemService itemService;

    public Order findById(Long id) {
        return orderRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("Order not found"));
    }

    public List<Order> findAll() {
        return orderRepository.findAll();
    }

    public Page<Order> findAllPageable(Pageable pageable) {
        return orderRepository.findAll(pageable);
    }

    public Order save(Order order) {
        return orderRepository.save(order);
    }

    @Transactional
    public Order createOrder(OrderCreateDTO orderDTO) {
        Customer customer = customerService.findById(orderDTO.getCustomerId());
        Order order = new Order(LocalDateTime.now(), OrderStatusEnum.PENDING, customer);

        for (OrderItemDTO itemDTO : orderDTO.getItems()) {
            Item product = itemService.findById(itemDTO.getProductId());
            
            if (product.getStock() < itemDTO.getQuantity()) {
                throw new IllegalStateException("Insufficient stock for product: " + product.getName());
            }
            
            OrderItem item = new OrderItem(order, product, itemDTO.getQuantity(), product.getPrice());
            
            order.getItems().add(item);
            
            product.setStock(product.getStock() - itemDTO.getQuantity());
            itemService.save(product);
        }
        
        return orderRepository.save(order);
    }

    @Transactional
    public Order removeItemFromOrder(Long orderId, Long itemId) {
        Order order = findById(orderId);
        Item item = itemService.findById(itemId);

        OrderItem orderItem = order.getItems().stream()
            .filter(oi -> oi.getItem().getId().equals(itemId))
            .findFirst()
            .orElseThrow(() -> new NotFoundException("Item not found in order"));

        order.getItems().remove(orderItem);

        item.setStock(item.getStock() + orderItem.getQuantity());
        itemService.save(item);

        return orderRepository.save(order);
    }

    @Transactional
    public Order addItemToOrder(Long orderId, OrderItemDTO itemDTO) {
        Order order = findById(orderId);
        Item item = itemService.findById(itemDTO.getProductId());

        if (item.getStock() < itemDTO.getQuantity()) {
            throw new IllegalStateException("Insufficient stock for product: " + item.getName());
        }

        OrderItem orderItem = order.getItems().stream()
                .filter(orderItemFounded -> orderItemFounded.getItem().getId().equals(itemDTO.getProductId()))
                .findFirst()
                .orElse(new OrderItem(order, item, itemDTO.getQuantity(), item.getPrice()));

        order.getItems().add(orderItem);

        item.setStock(item.getStock() - itemDTO.getQuantity());
        itemService.save(item);

        return orderRepository.save(order);
    }

    // TODO Response DTOs

    public void delete(Long id) {
        orderRepository.delete(findById(id));
    }
    
}

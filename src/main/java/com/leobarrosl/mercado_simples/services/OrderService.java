package com.leobarrosl.mercado_simples.services;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.leobarrosl.mercado_simples.exceptions.NotFoundException;
import com.leobarrosl.mercado_simples.models.dtos.OrderCreateDTO;
import com.leobarrosl.mercado_simples.models.dtos.OrderItemDTO;
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
        
        Order order = new Order();
        order.setCustomer(customer);
        order.setOrderDate(LocalDateTime.now());
        order.setStatus(OrderStatusEnum.PENDING);

        for (OrderItemDTO itemDTO : orderDTO.getItems()) {
            Item product = itemService.findById(itemDTO.getProductId());
            
            if (product.getStock() < itemDTO.getQuantity()) {
                throw new IllegalStateException("Insufficient stock for product: " + product.getName());
            }
            
            OrderItem item = new OrderItem();
            item.setOrder(order);
            item.setItem(product);
            item.setQuantity(itemDTO.getQuantity());
            item.setSalePrice(product.getPrice());
            
            order.getItems().add(item);
            
            product.setStock(product.getStock() - itemDTO.getQuantity());
            itemService.save(product);
        }
        
        return orderRepository.save(order);
    }

    // TODO Change status
    // TODO Remove item from order
    // TODO Add item to order
    // TODO Calculate total price
    // TODO Response DTOs

    public void delete(Long id) {
        orderRepository.delete(findById(id));
    }
    
}

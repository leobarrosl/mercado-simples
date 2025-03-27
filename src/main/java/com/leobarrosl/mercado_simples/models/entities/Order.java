package com.leobarrosl.mercado_simples.models.entities;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.leobarrosl.mercado_simples.models.enums.OrderStatusEnum;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@NoArgsConstructor
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime orderDate = LocalDateTime.now();

    @Enumerated(EnumType.STRING)
    private OrderStatusEnum status;

    @ManyToOne
    @JoinColumn(name = "customer_id")
    private Customer customer;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> items = new ArrayList<>();

    public Double getTotalPrice() {
        return items.stream()
            .mapToDouble(item -> item.getSalePrice() * item.getQuantity())
            .sum();
    }

    public Order(LocalDateTime orderDate, OrderStatusEnum status, Customer customer) {
        this.orderDate = orderDate;
        this.status = status;
        this.customer = customer;
    }
}

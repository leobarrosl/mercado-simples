package com.leobarrosl.mercado_simples.controllers;

import java.util.List;

import com.leobarrosl.mercado_simples.models.enums.OrderStatusEnum;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import com.leobarrosl.mercado_simples.models.entities.Customer;
import com.leobarrosl.mercado_simples.services.CustomerService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/customers")
public class CustomerController {

    private final CustomerService customerService;

    @GetMapping
    public List<Customer> findAll() {
        return customerService.findAll();
    }

    @GetMapping("/page")
    public Page<Customer> findAllPageable(Pageable pageable) {
        return customerService.findAllPageable(pageable);
    }

    @GetMapping("/{id}")
    public Customer findById(@PathVariable Long id) {
        return customerService.findById(id);
    }

    @PostMapping
    public Customer save(@RequestBody Customer client) {
        return customerService.save(client);
    }

    @PutMapping("/{id}")
    public Customer update(@PathVariable Long id, @RequestBody Customer client) {
        client.setId(id);
        return customerService.save(client);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        customerService.delete(id);
    }
}

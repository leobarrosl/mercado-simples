package com.leobarrosl.mercado_simples.controllers;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.leobarrosl.mercado_simples.models.entities.Customer;
import com.leobarrosl.mercado_simples.services.CustomerService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/clients")
public class CustomerController {

    private final CustomerService clientService;

    @GetMapping
    public List<Customer> findAll() {
        return clientService.findAll();
    }

    @GetMapping("/page")
    public Page<Customer> findAllPageable(Pageable pageable) {
        return clientService.findAllPageable(pageable);
    }

    @GetMapping("/{id}")
    public Customer findById(@PathVariable Long id) {
        return clientService.findById(id);
    }

    @PostMapping
    public Customer save(Customer client) {
        return clientService.save(client);
    }

    @PutMapping("/{id}")
    public Customer update(@PathVariable Long id, Customer client) {
        client.setId(id);
        return clientService.save(client);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        clientService.delete(id);
    }
}

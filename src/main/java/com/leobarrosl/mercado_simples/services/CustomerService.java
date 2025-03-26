package com.leobarrosl.mercado_simples.services;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.leobarrosl.mercado_simples.exceptions.NotFoundException;
import com.leobarrosl.mercado_simples.models.entities.Customer;
import com.leobarrosl.mercado_simples.repositories.CustomerRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerRepository clientRepository;

    public Customer findById(Long id) {
        return clientRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("Client not found"));
    }

    public List<Customer> findAll() {
        return clientRepository.findAll();
    }

    public Page<Customer> findAllPageable(Pageable pageable) {
        return clientRepository.findAll(pageable);
    }

    public Customer save(Customer client) {
        if (!client.isValid()) {
            throw new IllegalArgumentException("Invalid client");
        }
        return clientRepository.save(client);
    }

    public void delete(Long id) {
        clientRepository.delete(findById(id));
    }

}

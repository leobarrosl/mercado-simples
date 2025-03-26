package com.leobarrosl.mercado_simples.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.leobarrosl.mercado_simples.models.entities.Customer;

public interface CustomerRepository extends JpaRepository<Customer, Long> {

}

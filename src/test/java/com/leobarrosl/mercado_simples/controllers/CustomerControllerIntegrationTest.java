package com.leobarrosl.mercado_simples.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.leobarrosl.mercado_simples.models.entities.Customer;
import com.leobarrosl.mercado_simples.models.entities.Item;
import com.leobarrosl.mercado_simples.repositories.CustomerRepository;
import com.leobarrosl.mercado_simples.repositories.ItemRepository;
import com.leobarrosl.mercado_simples.repositories.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class CustomerControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CustomerRepository customerRepository;

    private Customer customer1;
    private Customer customer2;

    @BeforeEach
    void setUp() {
        customer1 = new Customer();
        customer1.setName("Test 1 Customer");
        customer1.setEmail("customer1@test.com");
        customerRepository.save(customer1);

        customer2 = new Customer();
        customer2.setName("Test 2 Customer");
        customer2.setEmail("customer2@test.com");
        customerRepository.save(customer2);
    }

    @Test
    void shouldReturnAllCustomers() throws Exception {
        mockMvc.perform(get("/customers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].name").value("Test 1 Customer"))
                .andExpect(jsonPath("$[1].name").value("Test 2 Customer"));
    }

    @Test
    void shouldReturnPageCustomers() throws Exception {
        mockMvc.perform(get("/customers/page"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.totalElements").value(2));
    }

    @Test
    void shouldFindCustomerById() throws Exception {
        mockMvc.perform(get("/customers/" + customer1.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Test 1 Customer"));
    }

    @Test
    void shouldCreateCustomer() throws Exception {
        Customer customer3 = new Customer();
        customer3.setName("Test 3 Customer");
        customer3.setEmail("customer3@test.com");
        customerRepository.save(customer3);

        mockMvc.perform(post("/customers")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(customer3)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Test 3 Customer"));
    }

    @Test
    void shouldUpdateCustomer() throws Exception {
        customer1.setName("Test 1 Customer Updated");
        mockMvc.perform(put("/customers/" + customer1.getId())
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(customer1)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Test 1 Customer Updated"));
    }

    @Test
    void shouldDeleteCustomer() throws Exception {
        mockMvc.perform(delete("/customers/" + customer2.getId()))
                .andExpect(status().isOk());

        mockMvc.perform(get("/customers/" + customer2.getId()))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturnErrorWhenCustomerNotFound() throws Exception {
        mockMvc.perform(get("/customers/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturnErrorWhenCreateInvalidCustomer() throws Exception {
        Customer customer3 = new Customer();
        customer3.setName("");
        customer3.setEmail("customer3@test.com");
        customerRepository.save(customer3);

        mockMvc.perform(post("/customers")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(customer3)))
                .andExpect(status().isBadRequest());
    }
}
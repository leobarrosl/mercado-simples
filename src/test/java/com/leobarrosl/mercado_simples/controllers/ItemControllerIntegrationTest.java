package com.leobarrosl.mercado_simples.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.leobarrosl.mercado_simples.models.entities.Customer;
import com.leobarrosl.mercado_simples.models.entities.Item;
import com.leobarrosl.mercado_simples.repositories.ItemRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class ItemControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ItemRepository itemRepository;

    private Item item1;
    private Item item2;

    @BeforeEach
    void setUp() {
        item1 = new Item();
        item1.setName("Item 1");
        item1.setPrice(10D);
        item1.setStock(10);
        item1.setDescription("Test Item 1");
        itemRepository.save(item1);

        item2 = new Item();
        item2.setName("Item 2");
        item2.setPrice(20D);
        item2.setStock(10);
        item2.setDescription("Test Item 2");
        itemRepository.save(item2);
    }

    @Test
    void shouldReturnAllItems() throws Exception {
        mockMvc.perform(get("/items"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].name").value("Item 1"))
                .andExpect(jsonPath("$[1].name").value("Item 2"));
    }

    @Test
    void shouldReturnPageItems() throws Exception {
        mockMvc.perform(get("/items/page"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.totalElements").value(2));
    }

    @Test
    void shouldFindItemById() throws Exception {
        mockMvc.perform(get("/items/" + item1.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Item 1"));
    }

    @Test
    void shouldCreateItem() throws Exception {
        Item item = new Item();
        item.setName("Item 3");
        item.setPrice(30D);
        item.setStock(10);
        item.setDescription("Test Item 3");

        mockMvc.perform(post("/items")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(item)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Item 3"));
    }

    @Test
    void shouldUpdateItem() throws Exception {
        item2.setName("Item 2 Updated");
        mockMvc.perform(put("/items/" + item1.getId())
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(item2)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Item 2 Updated"));
    }

    @Test
    void shouldDeleteItem() throws Exception {
        mockMvc.perform(delete("/items/" + item1.getId()))
                .andExpect(status().isOk());

        mockMvc.perform(get("/items/" + item1.getId()))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturnErrorWhenItemNotFound() throws Exception {
        mockMvc.perform(get("/items/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturnErrorWhenCreateInvalidItem() throws Exception {
        Item item = new Item();
        item.setName("");
        item.setPrice(30D);
        item.setStock(10);
        item.setDescription("Test Item 3");

        mockMvc.perform(post("/items")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(item)))
                .andExpect(status().isBadRequest());
    }
}
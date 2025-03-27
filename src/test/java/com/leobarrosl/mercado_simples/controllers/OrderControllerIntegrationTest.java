package com.leobarrosl.mercado_simples.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.leobarrosl.mercado_simples.models.dtos.reponses.OrderResponseDTO;
import com.leobarrosl.mercado_simples.models.dtos.requests.OrderCreateDTO;
import com.leobarrosl.mercado_simples.models.dtos.requests.OrderItemDTO;
import com.leobarrosl.mercado_simples.models.entities.Customer;
import com.leobarrosl.mercado_simples.models.entities.Item;
import com.leobarrosl.mercado_simples.models.entities.Order;
import com.leobarrosl.mercado_simples.models.enums.OrderStatusEnum;
import com.leobarrosl.mercado_simples.repositories.CustomerRepository;
import com.leobarrosl.mercado_simples.repositories.ItemRepository;
import com.leobarrosl.mercado_simples.repositories.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class OrderControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private OrderRepository orderRepository;

    private Customer customer;
    private Item item1;
    private Item item2;

    @BeforeEach
    void setup() {
        customer = new Customer();
        customer.setName("Test Customer");
        customer.setEmail("customer@test.com");
        customerRepository.save(customer);

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
    void shouldFindOrderById() throws Exception {
        OrderItemDTO orderItemDTO1 = new OrderItemDTO();
        orderItemDTO1.setProductId(item1.getId());
        orderItemDTO1.setQuantity(2);

        OrderCreateDTO orderCreateDTO = new OrderCreateDTO();
        orderCreateDTO.setCustomerId(customer.getId());
        orderCreateDTO.setItems(List.of(orderItemDTO1));

        String jsonOrderResponseDTO = mockMvc.perform(post("/orders")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(orderCreateDTO)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        OrderResponseDTO orderResponseDTO = objectMapper.readValue(jsonOrderResponseDTO, OrderResponseDTO.class);

        mockMvc.perform(get("/orders/" + orderResponseDTO.id()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(orderResponseDTO.id()))
                .andExpect(jsonPath("$.customerName").value(orderResponseDTO.customerName()));
    }

    @Test
    void shouldReturnPagedOrders() throws Exception {
        OrderItemDTO itemDTO = new OrderItemDTO();
        itemDTO.setProductId(item1.getId());
        itemDTO.setQuantity(1);

        OrderCreateDTO orderDTO = new OrderCreateDTO();
        orderDTO.setCustomerId(customer.getId());
        orderDTO.setItems(List.of(itemDTO));

        for (int i = 0; i < 5; i++) {
            mockMvc.perform(post("/orders")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(orderDTO)));
        }

        mockMvc.perform(get("/orders/page?page=0&size=2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.totalElements").value(5));
    }

    @Test
    void shouldChangeOrderStatus() throws Exception {
        OrderItemDTO itemDTO = new OrderItemDTO();
        itemDTO.setProductId(item1.getId());
        itemDTO.setQuantity(1);

        OrderCreateDTO orderDTO = new OrderCreateDTO();
        orderDTO.setCustomerId(customer.getId());
        orderDTO.setItems(List.of(itemDTO));

        String orderJson = mockMvc.perform(post("/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(orderDTO)))
                .andReturn().getResponse().getContentAsString();

        OrderResponseDTO createdOrder = objectMapper.readValue(orderJson, OrderResponseDTO.class);

        System.out.println(createdOrder.id());
        mockMvc.perform(patch("/orders/" + createdOrder.id())
                        .param("newStatus", OrderStatusEnum.DELIVERED.name()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(OrderStatusEnum.DELIVERED.name()));
    }

    @Test
    void shouldDeleteOrder() throws Exception {
        // Criar um pedido primeiro
        OrderItemDTO itemDTO = new OrderItemDTO();
        itemDTO.setProductId(item1.getId());
        itemDTO.setQuantity(1);

        OrderCreateDTO orderDTO = new OrderCreateDTO();
        orderDTO.setCustomerId(customer.getId());
        orderDTO.setItems(List.of(itemDTO));

        String orderJson = mockMvc.perform(post("/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(orderDTO)))
                .andReturn().getResponse().getContentAsString();

        Order createdOrder = objectMapper.readValue(orderJson, Order.class);

        mockMvc.perform(delete("/orders/" + createdOrder.getId()))
                .andExpect(status().isOk());

        mockMvc.perform(get("/orders/" + createdOrder.getId()))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldCreateOrderWithItems() throws Exception {
        OrderItemDTO orderItemDTO1 = new OrderItemDTO();
        orderItemDTO1.setProductId(item1.getId());
        orderItemDTO1.setQuantity(2);

        OrderCreateDTO orderCreateDTO = new OrderCreateDTO();
        orderCreateDTO.setCustomerId(customer.getId());
        orderCreateDTO.setItems(List.of(orderItemDTO1));

        mockMvc.perform(post("/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(orderCreateDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.items", hasSize(1)))
                .andExpect(jsonPath("$.customerName").value(customer.getName()))
                .andExpect(jsonPath("$.totalPrice").value(orderItemDTO1.getQuantity() * item1.getPrice()));
    }

    @Test
    void shouldAddItemToExistingOrder() throws Exception {
        OrderItemDTO initialItem = new OrderItemDTO();
        initialItem.setProductId(item1.getId());
        initialItem.setQuantity(1);

        OrderCreateDTO orderCreateDTO = new OrderCreateDTO();
        orderCreateDTO.setCustomerId(customer.getId());
        orderCreateDTO.setItems(List.of(initialItem));

        String orderJson = mockMvc.perform(post("/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(orderCreateDTO)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        OrderResponseDTO createdOrder = objectMapper.readValue(orderJson, OrderResponseDTO.class);

        OrderItemDTO newItem = new OrderItemDTO();
        newItem.setProductId(item2.getId());
        newItem.setQuantity(3);

        mockMvc.perform(post("/orders/" + createdOrder.id() + "/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newItem)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items", hasSize(2)))
                .andExpect(jsonPath("$.items[1].quantity").value(3));
    }

    @Test
    void shouldRemoveItemFromOrder() throws Exception {
        OrderItemDTO item1DTO = new OrderItemDTO();
        item1DTO.setProductId(item1.getId());
        item1DTO.setQuantity(2);

        OrderItemDTO item2DTO = new OrderItemDTO();
        item2DTO.setProductId(item2.getId());
        item2DTO.setQuantity(1);

        OrderCreateDTO orderCreateDTO = new OrderCreateDTO();
        orderCreateDTO.setCustomerId(customer.getId());
        orderCreateDTO.setItems(Arrays.asList(item1DTO, item2DTO));

        String orderJson = mockMvc.perform(post("/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(orderCreateDTO)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        OrderResponseDTO createdOrder = objectMapper.readValue(orderJson, OrderResponseDTO.class);

        mockMvc.perform(delete("/orders/" + createdOrder.id() + "/items/" + item1.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items", hasSize(1)));
    }

    @Test
    void shouldReturnAllOrders() throws Exception {
        OrderItemDTO itemDTO = new OrderItemDTO();
        itemDTO.setProductId(item1.getId());
        itemDTO.setQuantity(1);

        OrderCreateDTO orderCreateDTO = new OrderCreateDTO();
        orderCreateDTO.setCustomerId(customer.getId());
        orderCreateDTO.setItems(List.of(itemDTO));

        mockMvc.perform(post("/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(orderCreateDTO)));

        mockMvc.perform(post("/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(orderCreateDTO)));

        mockMvc.perform(get("/orders"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    void shouldReturnErrorWhenOrderNotFound() throws Exception {
        mockMvc.perform(get("/orders/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturnErrorWhenAddingInvalidItem() throws Exception {
        OrderItemDTO itemDTO = new OrderItemDTO();
        itemDTO.setProductId(item1.getId());
        itemDTO.setQuantity(1);

        OrderCreateDTO orderDTO = new OrderCreateDTO();
        orderDTO.setCustomerId(customer.getId());
        orderDTO.setItems(List.of(itemDTO));

        String orderJson = mockMvc.perform(post("/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(orderDTO)))
                .andReturn().getResponse().getContentAsString();

        Order createdOrder = objectMapper.readValue(orderJson, Order.class);

        OrderItemDTO invalidItem = new OrderItemDTO();
        invalidItem.setProductId(999L);
        invalidItem.setQuantity(1);

        mockMvc.perform(post("/orders/" + createdOrder.getId() + "/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidItem)))
                .andExpect(status().isNotFound());
    }
}
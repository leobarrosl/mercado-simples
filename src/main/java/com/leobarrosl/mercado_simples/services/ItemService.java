package com.leobarrosl.mercado_simples.services;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.leobarrosl.mercado_simples.exceptions.NotFoundException;
import com.leobarrosl.mercado_simples.models.entities.Item;
import com.leobarrosl.mercado_simples.repositories.ItemRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ItemService {

    private final ItemRepository itemRepository;

    public Item findById(Long id) {
        return itemRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("Item not found"));
    }

    public List<Item> findAll() {
        return itemRepository.findAll();
    }

    public Page<Item> findAllPageable(Pageable pageable) {
        return itemRepository.findAll(pageable);
    }

    public Item save(Item item) {
        if (!item.isValid()) {
            throw new IllegalArgumentException("Invalid item");
        }
        return itemRepository.save(item);
    }

    public void delete(Long id) {
        itemRepository.delete(findById(id));
    }
    
}

package com.programming.inventoryservice.service;

import com.programming.inventoryservice.model.Inventory;
import com.programming.inventoryservice.model.InventoryResponse;
import com.programming.inventoryservice.repository.InventoryRepository;
import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class InventoryService {

	@Autowired
    private InventoryRepository inventoryRepository;

    @Transactional(readOnly = true)
    public List<InventoryResponse> isInStock(List<String> skuCode) {
    	
        return inventoryRepository.findBySkuCodeIn(skuCode).stream()
        		.map(this::mapToDto)
        		.toList();
    }
    
    private InventoryResponse mapToDto(Inventory inventory)	{
    	
    	InventoryResponse inventoryResponse = new InventoryResponse();
    	inventoryResponse.setSkuCode(inventory.getSkuCode());
    	inventoryResponse.setInStock(inventory.getQuantity() > 0);
    	return inventoryResponse;
    	
    }
}

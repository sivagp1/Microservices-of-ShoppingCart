package com.programming.orderservice.service;

import com.programming.orderservice.dto.InventoryResponse;
import com.programming.orderservice.dto.OrderLineItemsDto;
import com.programming.orderservice.dto.OrderRequest;
import com.programming.orderservice.model.Order;
import com.programming.orderservice.model.OrderLineItems;
import com.programming.orderservice.repository.OrderRepository;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderService {

	@Autowired
    private OrderRepository orderRepository;
	
	@Autowired
	private WebClient.Builder webClientBuilder;
	

    public void placeOrder(OrderRequest orderRequest) {
        Order order = new Order();
        order.setOrderNumber(UUID.randomUUID().toString());

        List<OrderLineItems> orderLineItems = orderRequest.getOrderLineItemsDtoList()
                .stream()
                .map(this::mapToDto)
                .toList();

        order.setOrderLineItemsList(orderLineItems);
        List<String> skuCodes = order.getOrderLineItemsList().stream()
        		.map(OrderLineItems::getSkuCode)
        		.toList();
        		
        InventoryResponse[] invResponses = webClientBuilder.build().get()
        		.uri("http://inventory-service/api/inventory", 
        				uriBuilder -> uriBuilder.queryParam("skuCode", skuCodes).build())
        		.retrieve()
        		.bodyToMono(InventoryResponse[].class)
        		.block();
        
        Boolean productsInStock = Arrays.stream(invResponses).allMatch(InventoryResponse::isInStock);
        
        if(productsInStock)
        	orderRepository.save(order);
        else
        	throw new IllegalArgumentException("Product is not in stock. Try again later.");
    }

    private OrderLineItems mapToDto(OrderLineItemsDto orderLineItemsDto) {
        OrderLineItems orderLineItems = new OrderLineItems();
        orderLineItems.setPrice(orderLineItemsDto.getPrice());
        orderLineItems.setQuantity(orderLineItemsDto.getQuantity());
        orderLineItems.setSkuCode(orderLineItemsDto.getSkuCode());
        return orderLineItems;
    }
}

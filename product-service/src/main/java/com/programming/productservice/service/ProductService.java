package com.programming.productservice.service;

import com.programming.productservice.dto.ProductRequest;
import com.programming.productservice.dto.ProductResponse;
import com.programming.productservice.model.Product;
import com.programming.productservice.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductService {

	private static Logger log = LoggerFactory.getLogger(ProductService.class);
	
	@Autowired
    private ProductRepository productRepository;

    public void createProduct(ProductRequest productRequest) {
        Product product = mapToProduct(productRequest);

        productRepository.save(product);
        log.info("Product {} is saved", product.getId());
    }

    public List<ProductResponse> getAllProducts() {
        List<Product> products = productRepository.findAll();

        return products.stream().map(this::mapToProductResponse).toList();
    }
    
    private Product mapToProduct(ProductRequest productRequest)	{
    	Product product = new Product();
    	product.setName(productRequest.getName());
    	product.setDescription(productRequest.getDescription());
    	product.setPrice(productRequest.getPrice());
    	return product;
    }
    
    private ProductResponse mapToProductResponse(Product product)	{
    	ProductResponse productResponse = new ProductResponse();
    	productResponse.setId(product.getId());
    	productResponse.setName(product.getName());
    	productResponse.setDescription(product.getDescription());
    	productResponse.setPrice(product.getPrice());
    	return productResponse;
    }
}

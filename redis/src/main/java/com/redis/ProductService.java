package com.redis;

import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;

@Service
public class ProductService {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public void getProduct(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(EntityNotFoundException::new);



    }
}

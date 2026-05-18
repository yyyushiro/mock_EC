package com.example.springboot.controller;

import com.example.springboot.dto.ProductForCustomer;
import com.example.springboot.service.ProductService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/customer/products")
public class CustomerProductController {

    private final ProductService productService;

    public CustomerProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping("/{id}")
    public ProductForCustomer getProduct(@PathVariable long id) {
       return productService.getProductForCustomer(id);
    }
}

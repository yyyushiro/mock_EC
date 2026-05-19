package com.example.springboot.controller;

import com.example.springboot.dto.ProductForCustomer;
import com.example.springboot.service.CustomerProductService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/customer/products")
public class CustomerProductController {

    private final CustomerProductService customerProductService;

    public CustomerProductController(CustomerProductService customerProductService) {
        this.customerProductService = customerProductService;
    }

    @GetMapping("/{id}")
    public ProductForCustomer getProduct(@PathVariable long id) {
       return customerProductService.getProduct(id);
    }

    @GetMapping("/search/{name}")
    public List<ProductForCustomer> searchProductsByName(@PathVariable String name) {
        return customerProductService.searchProductsByName(name);
    }

    @GetMapping("/search/price-range")
    public List<ProductForCustomer> searchProductsByPrice(@RequestParam(required = false) Integer minPrice,
                                                          @RequestParam(required = false) Integer maxPrice) {
        return customerProductService.searchProductsByPrice(minPrice, maxPrice);
    }
}

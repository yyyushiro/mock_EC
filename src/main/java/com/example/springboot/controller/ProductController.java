package com.example.springboot.controller;

import com.example.springboot.entity.Product;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    @GetMapping("/{id}")
    public Product getProduct(@PathVariable int id) {
        return
    }
}

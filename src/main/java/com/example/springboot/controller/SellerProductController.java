package com.example.springboot.controller;

import com.example.springboot.dto.ProductForSellerCreateAndUpdateRequest;
import com.example.springboot.dto.ProductForSellerResponse;
import com.example.springboot.service.SellerProductService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/seller/products")
public class SellerProductController {

    private final SellerProductService sellerProductService;

    public SellerProductController(SellerProductService sellerProductService) {
        this.sellerProductService = sellerProductService;
    }

    @GetMapping("/{uuidV4}")
    public ProductForSellerResponse getProduct(@PathVariable UUID uuidV4) {
        return sellerProductService.getProduct(uuidV4);
    }

    @PostMapping("/")
    public ProductForSellerResponse addProduct(@RequestBody @Valid ProductForSellerCreateAndUpdateRequest productForSellerCreateRequest) {
        return sellerProductService.addProduct(productForSellerCreateRequest);
    }

    @PutMapping("/{uuidV4}")
    public void updateProduct(@PathVariable UUID uuidV4, @RequestBody @Valid ProductForSellerCreateAndUpdateRequest productForSellerCreateRequest) {
        sellerProductService.updateProduct(uuidV4, productForSellerCreateRequest);
    }

    @DeleteMapping("/{uuidV4}")
    public void deleteProduct(@PathVariable UUID uuidV4) {
        sellerProductService.deleteProduct(uuidV4);
    }

}

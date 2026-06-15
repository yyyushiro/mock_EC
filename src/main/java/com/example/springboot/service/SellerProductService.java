package com.example.springboot.service;

import com.example.springboot.dto.ProductForSellerCreateAndUpdateRequest;
import com.example.springboot.dto.ProductForSellerResponse;
import com.example.springboot.entity.Product;
import com.example.springboot.repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class SellerProductService {

    private final ProductRepository productRepository;

    public SellerProductService(ProductRepository productRepository) { this.productRepository = productRepository; }

    public ProductForSellerResponse getProduct(UUID uuidV4) {
        Optional<Product> product = productRepository.findByUuidV4(uuidV4);

        return product
                .map(p -> new ProductForSellerResponse(p.getUuidV4(), p.getName(), p.getPrice(), p.getStock()))
                .orElseThrow();
    }

    public ProductForSellerResponse addProduct(ProductForSellerCreateAndUpdateRequest productForSellerCreateRequest) {
        Product product = new Product();
        product.setName(productForSellerCreateRequest.name());
        product.setPrice(productForSellerCreateRequest.price());
        product.setStock(productForSellerCreateRequest.stock());

        System.out.println("Price確認: " + product.getPrice());

        Product productWithUuid = productRepository.save(product);

        return new ProductForSellerResponse(
                productWithUuid.getUuidV4(),
                productWithUuid.getName(),
                productWithUuid.getPrice(),
                productWithUuid.getStock()
        );
    }

    public void deleteProduct(UUID uuidV4) {
        productRepository.deleteByUuidV4(uuidV4);
    }

    public void updateProduct(UUID uuidV4, ProductForSellerCreateAndUpdateRequest productForSellerCreateRequest) {
        Product product = productRepository.findByUuidV4(uuidV4).orElseThrow();
        product.setName(productForSellerCreateRequest.name());
        product.setPrice(productForSellerCreateRequest.price());
        product.setStock(productForSellerCreateRequest.stock());

        productRepository.save(product);
    }
}

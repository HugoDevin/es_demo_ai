package com.example.ecommerce.service;

import com.example.ecommerce.dto.request.CreateProductRequest;
import com.example.ecommerce.dto.response.ProductResponse;
import com.example.ecommerce.model.Enums;
import org.springframework.data.domain.Page;

public interface ProductService {
    Page<ProductResponse> list(String keyword, int page, int size);
    ProductResponse get(Long id);
    ProductResponse create(CreateProductRequest req, String sellerUsername);
    ProductResponse update(Long id, CreateProductRequest req, String sellerUsername);
    ProductResponse updateStatus(Long id, Enums.ProductStatus status, String sellerUsername);
    void delete(Long id, String sellerUsername);
}

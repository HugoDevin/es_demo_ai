package com.example.ecommerce.controller;

import com.example.ecommerce.dto.request.CreateProductRequest;
import com.example.ecommerce.dto.response.ApiResponse;
import com.example.ecommerce.dto.response.ProductResponse;
import com.example.ecommerce.model.Enums;
import com.example.ecommerce.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/products")
public class ProductController {
    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public ApiResponse<Page<ProductResponse>> list(@RequestParam(defaultValue = "0") int page,
                                                   @RequestParam(defaultValue = "10") int size,
                                                   @RequestParam(required = false) String keyword) {
        return ApiResponse.ok(productService.list(keyword, page, size));
    }

    @GetMapping("/{id}")
    public ApiResponse<ProductResponse> get(@PathVariable Long id) {
        return ApiResponse.ok(productService.get(id));
    }

    @PostMapping
    public ApiResponse<ProductResponse> create(@RequestBody @Valid CreateProductRequest request, Authentication auth) {
        return ApiResponse.ok(productService.create(request, auth.getName()));
    }

    @PutMapping("/{id}")
    public ApiResponse<ProductResponse> update(@PathVariable Long id, @RequestBody @Valid CreateProductRequest request, Authentication auth) {
        return ApiResponse.ok(productService.update(id, request, auth.getName()));
    }

    @PatchMapping("/{id}/status")
    public ApiResponse<ProductResponse> patchStatus(@PathVariable Long id, @RequestBody Map<String, String> body, Authentication auth) {
        return ApiResponse.ok(productService.updateStatus(id, Enums.ProductStatus.valueOf(body.get("status")), auth.getName()));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id, Authentication auth) {
        productService.delete(id, auth.getName());
        return ApiResponse.okMessage("deleted");
    }
}

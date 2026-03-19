package com.example.ecommerce.service.impl;

import com.example.ecommerce.dto.request.CreateProductRequest;
import com.example.ecommerce.dto.response.ProductResponse;
import com.example.ecommerce.exception.BusinessException;
import com.example.ecommerce.model.Enums;
import com.example.ecommerce.model.Product;
import com.example.ecommerce.model.User;
import com.example.ecommerce.repository.ProductRepository;
import com.example.ecommerce.repository.UserRepository;
import com.example.ecommerce.service.ProductService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    public ProductServiceImpl(ProductRepository productRepository, UserRepository userRepository) {
        this.productRepository = productRepository;
        this.userRepository = userRepository;
    }

    @Override
    public Page<ProductResponse> list(String keyword, int page, int size) {
        return productRepository.findByStatusAndKeyword(Enums.ProductStatus.ACTIVE, keyword, PageRequest.of(page, size)).map(this::toResponse);
    }

    @Override
    public ProductResponse get(Long id) {
        return toResponse(productRepository.findByIdAndStatus(id, Enums.ProductStatus.ACTIVE)
                .orElseThrow(() -> new BusinessException("Product not found", HttpStatus.NOT_FOUND)));
    }

    @Override
    @Transactional
    public ProductResponse create(CreateProductRequest req, String sellerUsername) {
        User seller = loadSeller(sellerUsername);
        Product product = Product.builder().seller(seller).name(req.getName()).description(req.getDescription())
                .price(req.getPrice()).stock(req.getStock()).status(Enums.ProductStatus.ACTIVE).build();
        return toResponse(productRepository.save(product));
    }

    @Override
    @Transactional
    public ProductResponse update(Long id, CreateProductRequest req, String sellerUsername) {
        Product product = ownedProduct(id, sellerUsername);
        product.setName(req.getName());
        product.setDescription(req.getDescription());
        product.setPrice(req.getPrice());
        product.setStock(req.getStock());
        return toResponse(product);
    }

    @Override
    @Transactional
    public ProductResponse updateStatus(Long id, Enums.ProductStatus status, String sellerUsername) {
        Product product = ownedProduct(id, sellerUsername);
        product.setStatus(status);
        return toResponse(product);
    }

    @Override
    @Transactional
    public void delete(Long id, String sellerUsername) {
        Product product = ownedProduct(id, sellerUsername);
        product.setStatus(Enums.ProductStatus.INACTIVE);
    }

    private Product ownedProduct(Long id, String sellerUsername) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Product not found", HttpStatus.NOT_FOUND));
        if (!product.getSeller().getUsername().equals(sellerUsername)) {
            throw new BusinessException("Forbidden", HttpStatus.FORBIDDEN);
        }
        return product;
    }

    private User loadSeller(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new BusinessException("User not found", HttpStatus.NOT_FOUND));
        if (user.getRole() != Enums.Role.SELLER) {
            throw new BusinessException("Forbidden", HttpStatus.FORBIDDEN);
        }
        return user;
    }

    private ProductResponse toResponse(Product p) {
        return ProductResponse.builder().id(p.getId()).name(p.getName()).description(p.getDescription())
                .price(p.getPrice()).stock(p.getStock()).status(p.getStatus()).sellerId(p.getSeller().getId()).build();
    }
}

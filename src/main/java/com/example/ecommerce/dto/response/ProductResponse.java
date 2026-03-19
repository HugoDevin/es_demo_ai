package com.example.ecommerce.dto.response;

import com.example.ecommerce.model.Enums;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data @Builder
public class ProductResponse {
    private Long id;
    private String name;
    private String description;
    private BigDecimal price;
    private Integer stock;
    private Enums.ProductStatus status;
    private Long sellerId;
}

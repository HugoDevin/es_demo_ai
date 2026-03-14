package com.example.ecommerce.dto.response;

import com.example.ecommerce.model.Enums;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data @Builder
public class OrderResponse {
    private Long id;
    private Enums.OrderStatus status;
    private Enums.PaymentMethod paymentMethod;
    private String shippingAddress;
    private BigDecimal totalAmount;
    private LocalDateTime createdAt;
    private Long buyerId;
    private List<Item> items;

    @Data @Builder
    public static class Item {
        private Long productId;
        private String productName;
        private BigDecimal unitPrice;
        private Integer quantity;
        private BigDecimal subtotal;
    }
}

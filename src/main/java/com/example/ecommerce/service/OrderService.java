package com.example.ecommerce.service;

import com.example.ecommerce.dto.request.CreateOrderRequest;
import com.example.ecommerce.dto.response.OrderResponse;
import com.example.ecommerce.model.Enums;
import org.springframework.data.domain.Page;

public interface OrderService {
    OrderResponse create(CreateOrderRequest request, String buyerUsername);
    Page<OrderResponse> myOrders(String buyerUsername, int page, int size);
    OrderResponse getOrder(Long id, String username);
    OrderResponse updateStatus(Long id, Enums.OrderStatus status, String sellerUsername);
    void cancel(Long id, String buyerUsername);
}

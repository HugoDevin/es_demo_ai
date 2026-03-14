package com.example.ecommerce.controller;

import com.example.ecommerce.dto.request.CreateOrderRequest;
import com.example.ecommerce.dto.response.ApiResponse;
import com.example.ecommerce.dto.response.OrderResponse;
import com.example.ecommerce.model.Enums;
import com.example.ecommerce.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/orders")
public class OrderController {
    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    public ApiResponse<OrderResponse> create(@RequestBody @Valid CreateOrderRequest request, Authentication auth) {
        return ApiResponse.ok(orderService.create(request, auth.getName()));
    }

    @GetMapping
    public ApiResponse<Page<OrderResponse>> mine(@RequestParam(defaultValue = "0") int page,
                                                 @RequestParam(defaultValue = "10") int size,
                                                 Authentication auth) {
        return ApiResponse.ok(orderService.myOrders(auth.getName(), page, size));
    }

    @GetMapping("/{id}")
    public ApiResponse<OrderResponse> get(@PathVariable Long id, Authentication auth) {
        return ApiResponse.ok(orderService.getOrder(id, auth.getName()));
    }

    @PatchMapping("/{id}/status")
    public ApiResponse<OrderResponse> updateStatus(@PathVariable Long id, @RequestBody Map<String, String> body, Authentication auth) {
        return ApiResponse.ok(orderService.updateStatus(id, Enums.OrderStatus.valueOf(body.get("status")), auth.getName()));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> cancel(@PathVariable Long id, Authentication auth) {
        orderService.cancel(id, auth.getName());
        return ApiResponse.okMessage("cancelled");
    }
}

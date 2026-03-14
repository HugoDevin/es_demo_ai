package com.example.ecommerce.service.impl;

import com.example.ecommerce.dto.request.CreateOrderRequest;
import com.example.ecommerce.dto.response.OrderResponse;
import com.example.ecommerce.exception.BusinessException;
import com.example.ecommerce.model.*;
import com.example.ecommerce.payment.PaymentGateway;
import com.example.ecommerce.payment.PaymentResult;
import com.example.ecommerce.repository.OrderRepository;
import com.example.ecommerce.repository.ProductRepository;
import com.example.ecommerce.repository.UserRepository;
import com.example.ecommerce.service.OrderService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class OrderServiceImpl implements OrderService {
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
    private final Map<String, PaymentGateway> gateways;

    public OrderServiceImpl(UserRepository userRepository, ProductRepository productRepository, OrderRepository orderRepository, List<PaymentGateway> gateways) {
        this.userRepository = userRepository;
        this.productRepository = productRepository;
        this.orderRepository = orderRepository;
        this.gateways = gateways.stream().collect(Collectors.toMap(PaymentGateway::supportedMethod, Function.identity()));
    }

    @Override
    @Transactional
    public OrderResponse create(CreateOrderRequest request, String buyerUsername) {
        User buyer = userRepository.findByUsername(buyerUsername)
                .orElseThrow(() -> new BusinessException("User not found", HttpStatus.NOT_FOUND));
        if (buyer.getRole() != Enums.Role.BUYER) throw new BusinessException("Forbidden", HttpStatus.FORBIDDEN);

        Order order = new Order();
        order.setBuyer(buyer);
        order.setPaymentMethod(request.getPaymentMethod());
        order.setShippingAddress(request.getShippingAddress());
        order.setStatus(Enums.OrderStatus.PENDING);

        BigDecimal total = BigDecimal.ZERO;
        for (CreateOrderRequest.Item reqItem : request.getItems()) {
            Product product = productRepository.findByIdAndStatus(reqItem.getProductId(), Enums.ProductStatus.ACTIVE)
                    .orElseThrow(() -> new BusinessException("Product not found", HttpStatus.NOT_FOUND));
            int updated = productRepository.decreaseStockAtomically(product.getId(), reqItem.getQuantity());
            if (updated == 0) throw new BusinessException("Insufficient stock", HttpStatus.CONFLICT);
            BigDecimal subtotal = product.getPrice().multiply(BigDecimal.valueOf(reqItem.getQuantity()));
            total = total.add(subtotal);
            order.getItems().add(OrderItem.builder()
                    .order(order)
                    .product(product)
                    .productName(product.getName())
                    .unitPrice(product.getPrice())
                    .quantity(reqItem.getQuantity())
                    .subtotal(subtotal)
                    .build());
        }
        order.setTotalAmount(total);

        PaymentGateway gateway = gateways.get(request.getPaymentMethod().name());
        if (gateway == null) throw new BusinessException("Payment method unsupported", HttpStatus.BAD_REQUEST);
        PaymentResult paymentResult = gateway.process(order);
        if (!paymentResult.success()) throw new BusinessException(paymentResult.message(), HttpStatus.CONFLICT);

        return toResponse(orderRepository.save(order));
    }

    @Override
    public Page<OrderResponse> myOrders(String buyerUsername, int page, int size) {
        User buyer = userRepository.findByUsername(buyerUsername)
                .orElseThrow(() -> new BusinessException("User not found", HttpStatus.NOT_FOUND));
        if (buyer.getRole() != Enums.Role.BUYER) throw new BusinessException("Forbidden", HttpStatus.FORBIDDEN);
        return orderRepository.findByBuyerId(buyer.getId(), PageRequest.of(page, size)).map(this::toResponse);
    }

    @Override
    public OrderResponse getOrder(Long id, String username) {
        Order order = orderRepository.findWithItemsById(id)
                .orElseThrow(() -> new BusinessException("Order not found", HttpStatus.NOT_FOUND));
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new BusinessException("User not found", HttpStatus.NOT_FOUND));
        if (user.getRole() == Enums.Role.BUYER && !order.getBuyer().getId().equals(user.getId())) {
            throw new BusinessException("Forbidden", HttpStatus.FORBIDDEN);
        }
        return toResponse(order);
    }

    @Override
    @Transactional
    public OrderResponse updateStatus(Long id, Enums.OrderStatus status, String sellerUsername) {
        User seller = userRepository.findByUsername(sellerUsername)
                .orElseThrow(() -> new BusinessException("User not found", HttpStatus.NOT_FOUND));
        if (seller.getRole() != Enums.Role.SELLER) throw new BusinessException("Forbidden", HttpStatus.FORBIDDEN);
        Order order = orderRepository.findWithItemsById(id)
                .orElseThrow(() -> new BusinessException("Order not found", HttpStatus.NOT_FOUND));
        order.setStatus(status);
        return toResponse(order);
    }

    @Override
    @Transactional
    public void cancel(Long id, String buyerUsername) {
        User buyer = userRepository.findByUsername(buyerUsername)
                .orElseThrow(() -> new BusinessException("User not found", HttpStatus.NOT_FOUND));
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Order not found", HttpStatus.NOT_FOUND));
        if (!order.getBuyer().getId().equals(buyer.getId())) throw new BusinessException("Forbidden", HttpStatus.FORBIDDEN);
        if (order.getStatus() != Enums.OrderStatus.PENDING) throw new BusinessException("Only pending order can be cancelled", HttpStatus.CONFLICT);
        order.setStatus(Enums.OrderStatus.CANCELLED);
    }

    private OrderResponse toResponse(Order order) {
        return OrderResponse.builder()
                .id(order.getId())
                .buyerId(order.getBuyer().getId())
                .status(order.getStatus())
                .paymentMethod(order.getPaymentMethod())
                .shippingAddress(order.getShippingAddress())
                .totalAmount(order.getTotalAmount())
                .createdAt(order.getCreatedAt())
                .items(order.getItems().stream().map(item -> OrderResponse.Item.builder()
                        .productId(item.getProduct().getId())
                        .productName(item.getProductName())
                        .unitPrice(item.getUnitPrice())
                        .quantity(item.getQuantity())
                        .subtotal(item.getSubtotal()).build()).toList())
                .build();
    }
}

package com.example.ecommerce.payment;

import com.example.ecommerce.model.Order;

public interface PaymentGateway {
    PaymentResult process(Order order);
    String supportedMethod();
}

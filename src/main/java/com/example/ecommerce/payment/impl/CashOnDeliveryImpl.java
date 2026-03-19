package com.example.ecommerce.payment.impl;

import com.example.ecommerce.model.Order;
import com.example.ecommerce.payment.PaymentGateway;
import com.example.ecommerce.payment.PaymentResult;
import org.springframework.stereotype.Component;

@Component
public class CashOnDeliveryImpl implements PaymentGateway {
    @Override
    public PaymentResult process(Order order) {
        return new PaymentResult(true, "cash on delivery accepted");
    }

    @Override
    public String supportedMethod() {
        return "CASH_ON_DELIVERY";
    }
}

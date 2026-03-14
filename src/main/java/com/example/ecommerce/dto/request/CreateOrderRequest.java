package com.example.ecommerce.dto.request;

import com.example.ecommerce.model.Enums;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class CreateOrderRequest {
    @NotEmpty @Valid
    private List<Item> items;
    @NotBlank
    private String shippingAddress;
    @NotNull
    private Enums.PaymentMethod paymentMethod;

    @Data
    public static class Item {
        @NotNull
        private Long productId;
        @NotNull
        private Integer quantity;
    }
}

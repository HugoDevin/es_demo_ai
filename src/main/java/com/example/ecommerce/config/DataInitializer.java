package com.example.ecommerce.config;

import com.example.ecommerce.model.Enums;
import com.example.ecommerce.model.Order;
import com.example.ecommerce.model.OrderItem;
import com.example.ecommerce.model.Product;
import com.example.ecommerce.model.User;
import com.example.ecommerce.repository.OrderRepository;
import com.example.ecommerce.repository.ProductRepository;
import com.example.ecommerce.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;
import java.util.List;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner seedDemoData(UserRepository userRepository,
                                   ProductRepository productRepository,
                                   OrderRepository orderRepository,
                                   PasswordEncoder passwordEncoder) {
        return args -> {
            if (userRepository.count() > 0 || productRepository.count() > 0 || orderRepository.count() > 0) {
                return;
            }

            User seller = userRepository.save(User.builder()
                    .username("seller_demo")
                    .password(passwordEncoder.encode("password123"))
                    .role(Enums.Role.SELLER)
                    .build());

            User buyer = userRepository.save(User.builder()
                    .username("buyer_demo")
                    .password(passwordEncoder.encode("password123"))
                    .role(Enums.Role.BUYER)
                    .build());

            Product keyboard = productRepository.save(Product.builder()
                    .seller(seller)
                    .name("Mechanical Keyboard")
                    .description("87-key hot-swappable mechanical keyboard for interview demo")
                    .price(new BigDecimal("2490.00"))
                    .stock(17)
                    .status(Enums.ProductStatus.ACTIVE)
                    .build());

            Product mouse = productRepository.save(Product.builder()
                    .seller(seller)
                    .name("Wireless Mouse")
                    .description("Ergonomic dual-mode wireless mouse")
                    .price(new BigDecimal("890.00"))
                    .stock(30)
                    .status(Enums.ProductStatus.ACTIVE)
                    .build());

            productRepository.save(Product.builder()
                    .seller(seller)
                    .name("27-inch Monitor")
                    .description("2K IPS monitor for product showcase")
                    .price(new BigDecimal("6990.00"))
                    .stock(7)
                    .status(Enums.ProductStatus.ACTIVE)
                    .build());

            productRepository.save(Product.builder()
                    .seller(seller)
                    .name("Archived Demo Product")
                    .description("Inactive sample product for soft delete demonstration")
                    .price(new BigDecimal("199.00"))
                    .stock(0)
                    .status(Enums.ProductStatus.INACTIVE)
                    .build());

            Order order = new Order();
            order.setBuyer(buyer);
            order.setStatus(Enums.OrderStatus.CONFIRMED);
            order.setPaymentMethod(Enums.PaymentMethod.CASH_ON_DELIVERY);
            order.setShippingAddress("No. 1, Demo Road, Taipei City");

            OrderItem item1 = OrderItem.builder()
                    .order(order)
                    .product(keyboard)
                    .productName(keyboard.getName())
                    .unitPrice(keyboard.getPrice())
                    .quantity(1)
                    .subtotal(keyboard.getPrice())
                    .build();

            OrderItem item2 = OrderItem.builder()
                    .order(order)
                    .product(mouse)
                    .productName(mouse.getName())
                    .unitPrice(mouse.getPrice())
                    .quantity(2)
                    .subtotal(mouse.getPrice().multiply(BigDecimal.valueOf(2)))
                    .build();

            order.setItems(new java.util.ArrayList<>(List.of(item1, item2)));
            order.setTotalAmount(item1.getSubtotal().add(item2.getSubtotal()));
            orderRepository.save(order);
        };
    }
}

package com.example.ecommerce.repository;

import com.example.ecommerce.model.Enums;
import com.example.ecommerce.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {
    @Query("select p from Product p where p.status = :status and (:keyword is null or lower(p.name) like lower(concat('%', :keyword, '%')))")
    Page<Product> findByStatusAndKeyword(Enums.ProductStatus status, String keyword, Pageable pageable);

    Optional<Product> findByIdAndStatus(Long id, Enums.ProductStatus status);

    @Modifying
    @Query("update Product p set p.stock = p.stock - :quantity where p.id = :productId and p.stock >= :quantity and p.status='ACTIVE'")
    int decreaseStockAtomically(Long productId, Integer quantity);
}

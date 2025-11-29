package org.example.editors_uz.repository;

import org.example.editors_uz.entity.OrderType;
import org.example.editors_uz.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Integer> {
    List<Product> findAllByOrderType(OrderType type);
}
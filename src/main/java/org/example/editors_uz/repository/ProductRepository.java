package org.example.editors_uz.repository;

import org.example.editors_uz.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Integer> {
}
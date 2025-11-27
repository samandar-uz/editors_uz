package org.example.editors_uz.repository;

import org.example.editors_uz.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Integer> {

    List<Product> findByPriceOrderByCreateTimeDesc(String price);

    @Query("SELECT p FROM Product p ORDER BY p.createTime DESC")
    List<Product> findAllOrderByCreateTimeDesc();
}
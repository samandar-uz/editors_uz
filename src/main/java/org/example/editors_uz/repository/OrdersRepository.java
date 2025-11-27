package org.example.editors_uz.repository;


import org.example.editors_uz.entity.Orders;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrdersRepository extends JpaRepository<Orders, Integer> {
    boolean existsByUserIdAndTemplateId(Integer userId, Integer templateId);
}
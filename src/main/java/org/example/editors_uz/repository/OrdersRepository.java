
package org.example.editors_uz.repository;

import org.example.editors_uz.entity.Orders;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrdersRepository extends JpaRepository<Orders, Integer> {
    boolean existsByUserIdAndTemplateId(Integer userId, Integer templateId);

    List<Orders> findAllByUserId(Integer userId);

}
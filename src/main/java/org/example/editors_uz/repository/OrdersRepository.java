
package org.example.editors_uz.repository;

import org.example.editors_uz.entity.Orders;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OrdersRepository extends JpaRepository<Orders, Integer> {
    boolean existsByUserIdAndProductId(Integer userId, Integer templateId);
    Optional<Orders> findByUserIdAndProductId(Integer userId, Integer productId);


}
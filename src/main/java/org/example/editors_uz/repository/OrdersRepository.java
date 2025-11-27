
package org.example.editors_uz.repository;

import org.example.editors_uz.entity.Orders;
import org.example.editors_uz.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrdersRepository extends JpaRepository<Orders, Integer> {
    boolean existsByUserIdAndProductId(Integer userId, Integer templateId);
    List<Orders> findByUser(User user);

}
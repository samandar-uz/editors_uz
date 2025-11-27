package org.example.editors_uz.repository;

import org.example.editors_uz.entity.Templates;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface TemplatesRepository extends JpaRepository<Templates, Integer> {

    List<Templates> findByPriceOrderByCreateTimeDesc(String price);

    @Query("SELECT t FROM Templates t ORDER BY t.createTime DESC")
    List<Templates> findAllOrderByCreateTimeDesc();

    @Query("SELECT t FROM Templates t WHERE LOWER(t.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(t.description) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Templates> searchTemplates(String keyword);
}
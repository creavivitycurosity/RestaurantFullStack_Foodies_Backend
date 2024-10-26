package com.example.practice.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.practice.entity.Category;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    Optional<Category> findByName(String name);
    
    @Query("SELECT DISTINCT c FROM Category c JOIN Item i ON c.id = i.category.id WHERE i.sellerName = :sellerEmail")
    List<Category> findCategoriesBySellerEmail(@Param("sellerEmail") String sellerEmail);
}

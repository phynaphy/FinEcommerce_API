package com.example.FIN_ecommerce_API.repository;

import com.example.FIN_ecommerce_API.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
}
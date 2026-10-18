package com.example.FIN_ecommerce_API.repository;

import com.example.FIN_ecommerce_API.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    boolean existsByName(String name);

    List<Category> findByIsRecommendedTrueOrderByRecommendationOrderAsc();

    List<Category> findByIsRecommendedTrue();

}
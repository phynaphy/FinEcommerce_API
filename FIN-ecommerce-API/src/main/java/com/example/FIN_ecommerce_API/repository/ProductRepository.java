package com.example.FIN_ecommerce_API.repository;

import com.example.FIN_ecommerce_API.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

//    @Query(value = "SELECT p FROM Product p JOIN FETCH p.category",
//            countQuery = "SELECT count(p) FROM Product p")
//    Page<Product> findAllWithCategory(Pageable pageable);

    @Query("SELECT p FROM Product p JOIN FETCH p.category")
    List<Product> findAllWithCategory();

    List<Product> findByCategoryId(Long categoryId);
}
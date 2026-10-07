package com.example.FIN_ecommerce_API.repository;

import com.example.FIN_ecommerce_API.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface AdminReportProductRepository extends JpaRepository<Product, Long> {
    @Query("select distinct p from Product p left join fetch p.category left join fetch p.variants order by p.id desc")
    List<Product> findAllForReport();
}

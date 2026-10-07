package com.example.FIN_ecommerce_API.repository;

import com.example.FIN_ecommerce_API.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface AdminReportCategoryRepository extends JpaRepository<Category, Long> {
    @Query("select c.id, c.name, count(p.id) from Category c left join Product p on p.category.id = c.id group by c.id, c.name order by count(p.id) desc, c.name asc")
    List<Object[]> getCategoryReport();
}

package com.example.FIN_ecommerce_API.repository;

import com.example.FIN_ecommerce_API.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface AdminReportUserRepository extends JpaRepository<User, Long> {
    @Query("select count(u) from User u where u.role = com.example.FIN_ecommerce_API.model.Role.ADMIN")
    long countAdminUsers();
}

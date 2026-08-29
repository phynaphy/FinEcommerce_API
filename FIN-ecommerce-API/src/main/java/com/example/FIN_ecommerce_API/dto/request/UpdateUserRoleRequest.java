package com.example.FIN_ecommerce_API.dto.request;

import com.example.FIN_ecommerce_API.model.Role;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateUserRoleRequest {

    @NotNull(message = "Role is required")
    private Role role; // ADMIN or USER
}
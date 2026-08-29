package com.example.FIN_ecommerce_API.dto.request;

import com.example.FIN_ecommerce_API.model.Role;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateUserRequest {
    private String fullName;
    private String email;
    private Role role;
}
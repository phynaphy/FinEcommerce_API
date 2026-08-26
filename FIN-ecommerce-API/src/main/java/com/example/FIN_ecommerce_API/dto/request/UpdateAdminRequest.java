package com.example.FIN_ecommerce_API.dto.request;

import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateAdminRequest {

    private String fullName;

    @Email(message = "Invalid email format")
    private String email;
}
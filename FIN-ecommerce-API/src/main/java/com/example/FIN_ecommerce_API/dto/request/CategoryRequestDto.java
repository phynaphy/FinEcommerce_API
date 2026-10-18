package com.example.FIN_ecommerce_API.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CategoryRequestDto {

    @NotBlank(message = "Category name is required")
    private String name;

    private String code;

    private MultipartFile imageUrl;

    private boolean isRecommended;
    private Integer recommendationOrder;
}
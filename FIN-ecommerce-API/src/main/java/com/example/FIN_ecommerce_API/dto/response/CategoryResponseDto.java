package com.example.FIN_ecommerce_API.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoryResponseDto {

    private Long id;
    private String code;
    private String name;
    private String imageUrl;
    private int totalProducts;

    @JsonProperty("isRecommended")
    private boolean isRecommended;
}
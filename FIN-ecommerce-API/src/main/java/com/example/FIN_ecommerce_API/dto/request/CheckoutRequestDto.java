package com.example.FIN_ecommerce_API.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CheckoutRequestDto {
    private String shippingAddress;
    private String phoneNumber;
    private String paymentMethod;
    private String notes;
}
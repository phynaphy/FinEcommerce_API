package com.example.FIN_ecommerce_API.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PayWayResponseDto {
    private String reqTime;
    private String merchantId;
    private String tranId;
    private String amount;
    private String firstname;
    private String lastname;
    private String email;
    private String phone;
    private String type;
    private String paymentOption;
    private String items;
    private String hash;
    private String continueSuccessUrl;
}
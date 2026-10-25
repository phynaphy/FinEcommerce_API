package com.example.FIN_ecommerce_API.service;

import com.example.FIN_ecommerce_API.dto.checkout.CheckoutRequestDto;
import com.example.FIN_ecommerce_API.dto.checkout.OrderResponseDto;

public interface CheckoutTestService {

    OrderResponseDto processCheckout(CheckoutRequestDto request);
}
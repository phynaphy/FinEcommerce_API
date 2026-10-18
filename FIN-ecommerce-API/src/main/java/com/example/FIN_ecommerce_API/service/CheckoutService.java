package com.example.FIN_ecommerce_API.service;

import com.example.FIN_ecommerce_API.dto.request.CheckoutRequestDto;
import com.example.FIN_ecommerce_API.dto.response.OrderResponseDto;

import java.util.List;

public interface CheckoutService {
    OrderResponseDto processCheckout(CheckoutRequestDto request);
    void updateOrderStatusByTrackingNumber(String trackingNumber, String status);
    List<OrderResponseDto> getMyOrders();
}
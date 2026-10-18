package com.example.FIN_ecommerce_API.controller;

import com.example.FIN_ecommerce_API.dto.request.CheckoutRequestDto;
import com.example.FIN_ecommerce_API.dto.response.OrderResponseDto;
import com.example.FIN_ecommerce_API.dto.response.PayWayCallbackDto;
import com.example.FIN_ecommerce_API.dto.response.PayWayResponseDto;
import com.example.FIN_ecommerce_API.service.CheckoutService;
import com.example.FIN_ecommerce_API.service.serviceImpl.PayWayService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;

@RestController
@RequestMapping("/api/v1/payway")
@RequiredArgsConstructor
public class PayWayController {

    private final CheckoutService checkoutService;
    private final PayWayService payWayService;

    @PostMapping("/purchase")
    public ResponseEntity<PayWayResponseDto> createPurchase(@RequestBody CheckoutRequestDto request) {
        OrderResponseDto order = checkoutService.processCheckout(request);

        String reqTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String tranId = order.getTrackingNumber();
        String amount = order.getTotalAmount().setScale(2).toString();
        String firstName = "Customer";
        String lastName = "User";
        String email = "customer@ecommerce.com";
        String phone = order.getPhoneNumber() != null ? order.getPhoneNumber() : "012345678";
        String type = "purchase";
        String paymentOption = "abapay_khqr";

        String rawItemsJson = "[{\"name\":\"Order Items\",\"quantity\":\"1\",\"price\":\"" + amount + "\"}]";
        String itemsBase64 = Base64.getEncoder().encodeToString(rawItemsJson.getBytes());

        String dataToHash = reqTime + payWayService.getMerchantId() + tranId + amount
                + firstName + lastName + email + phone + type + paymentOption + itemsBase64;

        String hash = payWayService.generateHMAC512(dataToHash);

        PayWayResponseDto response = PayWayResponseDto.builder()
                .reqTime(reqTime)
                .merchantId(payWayService.getMerchantId())
                .tranId(tranId)
                .amount(amount)
                .firstname(firstName)
                .lastname(lastName)
                .email(email)
                .phone(phone)
                .type(type)
                .paymentOption(paymentOption)
                .items(itemsBase64)
                .hash(hash)
                .continueSuccessUrl("http://localhost:4200/checkout/success")
                .build();

        return ResponseEntity.ok(response);
    }

    @PostMapping("/callback")
    public ResponseEntity<String> handlePayWayCallback(@RequestBody PayWayCallbackDto callback) {
        if ("0".equals(callback.getStatus()) || "PAID".equalsIgnoreCase(callback.getStatus())) {
            checkoutService.updateOrderStatusByTrackingNumber(callback.getTranId(), "PAID");
        }
        return ResponseEntity.ok("OK");
    }
}
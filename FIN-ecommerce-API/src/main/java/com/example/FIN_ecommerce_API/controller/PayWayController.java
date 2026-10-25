//package com.example.FIN_ecommerce_API.controller;
//
//import com.example.FIN_ecommerce_API.dto.request.CheckoutRequestDto;
//import com.example.FIN_ecommerce_API.dto.response.OrderResponseDto;
//import com.example.FIN_ecommerce_API.dto.response.PayWayResponseDto;
//import com.example.FIN_ecommerce_API.model.User;
//import com.example.FIN_ecommerce_API.repository.UserRepository;
//import com.example.FIN_ecommerce_API.service.CheckoutService;
//import com.example.FIN_ecommerce_API.service.serviceImpl.PayWayService;
//import lombok.RequiredArgsConstructor;
//import org.springframework.http.ResponseEntity;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.web.bind.annotation.*;
//
//import java.time.LocalDateTime;
//import java.time.format.DateTimeFormatter;
//import java.util.Base64;
//import java.util.HashMap;
//import java.util.Map;
//
//@RestController
//@RequestMapping("/api/v1/payway")
//@RequiredArgsConstructor
//public class PayWayController {
//
//    private final CheckoutService checkoutService;
//    private final PayWayService payWayService;
//    private final UserRepository userRepository;
//
//    @PostMapping("/purchase")
//    public ResponseEntity<PayWayResponseDto> createPurchase(@RequestBody CheckoutRequestDto request) {
//        // 1. Process checkout and retrieve OrderResponseDto
//        OrderResponseDto order = checkoutService.processCheckout(request);
//
//        // 2. Retrieve user details for ABA PayWay compliance
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        String currentUsername = authentication.getName();
//        User user = userRepository.findByUsername(currentUsername).orElse(null);
//
//        String userEmail = (user != null && user.getEmail() != null) ? user.getEmail() : "customer@ecommerce.com";
//        String fullName = (user != null && user.getFullName() != null) ? user.getFullName() : "Valued Customer";
//        String[] nameParts = fullName.trim().split("\\s+", 2);
//        String firstName = nameParts[0];
//        String lastName = nameParts.length > 1 ? nameParts[1] : "Customer";
//
//        // 3. Extract totalAmount directly from OrderResponseDto (BigDecimal -> String formatted to 2 decimals)
//        String amount = order.getTotalAmount() != null
//                ? order.getTotalAmount().setScale(2, java.math.RoundingMode.HALF_UP).toString()
//                : "0.00";
//
//        String reqTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
//        String merchantId = payWayService.getMerchantId();
//        String tranId = order.getTrackingNumber();
//        String phone = order.getPhoneNumber() != null ? order.getPhoneNumber() : "012345678";
//        String type = "purchase";
//        String paymentOption = "abapay_khqr_deeplink";
//        String currency = "USD";
//        String continueSuccessUrl = "http://localhost:4200/checkout/success";
//
//        // Encode line items
//        String rawItemsJson = "[{\"name\":\"Order " + tranId + "\",\"quantity\":\"1\",\"price\":\"" + amount + "\"}]";
//        String itemsBase64 = Base64.getEncoder().encodeToString(rawItemsJson.getBytes());
//
//        // 4. Concatenate Hash values matching official ABA PayWay sequence
//        String dataToHash = reqTime + merchantId + tranId + amount + itemsBase64 + ""
//                + firstName + lastName + userEmail + phone + type + paymentOption
//                + "" + "" + continueSuccessUrl + ""
//                + currency + "" + "" + "" + ""
//                + "" + "" + "";
//
//        String hash = payWayService.generateHMAC512(dataToHash);
//
//        // Prepare ABA PayWay request
//        Map<String, Object> payWayReq = new HashMap<>();
//        payWayReq.put("req_time", reqTime);
//        payWayReq.put("merchant_id", merchantId);
//        payWayReq.put("tran_id", tranId);
//        payWayReq.put("amount", amount);
//        payWayReq.put("items", itemsBase64);
//        payWayReq.put("firstname", firstName);
//        payWayReq.put("lastname", lastName);
//        payWayReq.put("email", userEmail);
//        payWayReq.put("phone", phone);
//        payWayReq.put("type", type);
//        payWayReq.put("payment_option", paymentOption);
//        payWayReq.put("currency", currency);
//        payWayReq.put("continue_success_url", continueSuccessUrl);
//        payWayReq.put("hash", hash);
//
//        // Execute API call to ABA
//        PayWayResponseDto response = payWayService.executePayWayPurchase(payWayReq);
//
//        // 5. Populate amount and merchantName dynamically for Angular UI display
//        response.setAmount(amount);
//        response.setMerchantName(fullName);
//
//        return ResponseEntity.ok(response);
//    }
//}
package com.example.FIN_ecommerce_API.controller;

import com.example.FIN_ecommerce_API.dto.request.CheckoutRequestDto;
import com.example.FIN_ecommerce_API.dto.response.OrderResponseDto;
import com.example.FIN_ecommerce_API.dto.response.PayWayResponseDto;
import com.example.FIN_ecommerce_API.model.User;
import com.example.FIN_ecommerce_API.repository.UserRepository;
import com.example.FIN_ecommerce_API.service.CheckoutService;
import com.example.FIN_ecommerce_API.service.serviceImpl.PayWayService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/payway")
@RequiredArgsConstructor
public class PayWayController {

    private final CheckoutService checkoutService;
    private final PayWayService payWayService;
    private final UserRepository userRepository;

    @PostMapping("/purchase")
    public ResponseEntity<PayWayResponseDto> createPurchase(@RequestBody CheckoutRequestDto request) {
        // 1. Process cart checkout dynamically and get OrderResponseDto
        OrderResponseDto order = checkoutService.processCheckout(request);

        // 2. Fetch authenticated logged-in user dynamically
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();
        User user = userRepository.findByUsername(currentUsername).orElse(null);

        String userEmail = (user != null && user.getEmail() != null) ? user.getEmail() : "customer@ecommerce.com";
        String fullName = (user != null && user.getFullName() != null) ? user.getFullName() : "Valued Customer";

        String[] nameParts = fullName.trim().split("\\s+", 2);
        String firstName = nameParts[0];
        String lastName = nameParts.length > 1 ? nameParts[1] : "Customer";

        // 3. Format dynamic amount directly from order.getTotalAmount() (e.g. 59.99)
        String amount = (order.getTotalAmount() != null)
                ? order.getTotalAmount().setScale(2, java.math.RoundingMode.HALF_UP).toString()
                : "0.00";

        String reqTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String merchantId = payWayService.getMerchantId();
        String tranId = order.getTrackingNumber();
        String phone = (order.getPhoneNumber() != null && !order.getPhoneNumber().isEmpty())
                ? order.getPhoneNumber()
                : "012345678";
        String type = "purchase";
        String paymentOption = "abapay_khqr_deeplink";
        String currency = "USD";
        String continueSuccessUrl = "http://localhost:8080/api/v1/payway/success";

        // Base64 Items JSON for ABA
        String rawItemsJson = "[{\"name\":\"Order " + tranId + "\",\"quantity\":\"1\",\"price\":\"" + amount + "\"}]";
        String itemsBase64 = Base64.getEncoder().encodeToString(rawItemsJson.getBytes());

        // Construct HMAC Hash sequence
        String dataToHash = reqTime + merchantId + tranId + amount + itemsBase64 + ""
                + firstName + lastName + userEmail + phone + type + paymentOption
                + "" + "" + continueSuccessUrl + ""
                + currency + "" + "" + "" + ""
                + "" + "" + "";

        String hash = payWayService.generateHMAC512(dataToHash);

        // Build Payload to Send to PayWay Engine
        Map<String, Object> payWayReq = new HashMap<>();
        payWayReq.put("req_time", reqTime);
        payWayReq.put("merchant_id", merchantId);
        payWayReq.put("tran_id", tranId);
        payWayReq.put("amount", amount);
        payWayReq.put("items", itemsBase64);
        payWayReq.put("firstname", firstName);
        payWayReq.put("lastname", lastName);
        payWayReq.put("email", userEmail);
        payWayReq.put("phone", phone);
        payWayReq.put("type", type);
        payWayReq.put("payment_option", paymentOption);
        payWayReq.put("currency", currency);
        payWayReq.put("continue_success_url", continueSuccessUrl);
        payWayReq.put("hash", hash);

        // Execute Request to ABA Sandbox / Production API
        PayWayResponseDto response = payWayService.executePayWayPurchase(payWayReq);

        // 4. Attach dynamic fields to ensure response matches your exact JSON payload
        response.setAmount(amount);
        response.setMerchantName(fullName);

        return ResponseEntity.ok(response);
    }
}
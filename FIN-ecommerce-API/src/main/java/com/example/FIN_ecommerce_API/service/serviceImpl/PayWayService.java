//package com.example.FIN_ecommerce_API.service.serviceImpl;
//
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.stereotype.Service;
//
//import javax.crypto.Mac;
//import javax.crypto.spec.SecretKeySpec;
//import java.nio.charset.StandardCharsets;
//import java.util.Base64;
//
//@Service
//public class PayWayService {
//
//    @Value("${payway.merchant-id}")
//    private String merchantId;
//
//    @Value("${payway.api-key}")
//    private String apiKey;
//
//    public String generateHMAC512(String data) {
//        try {
//            Mac sha512Hmac = Mac.getInstance("HmacSHA512");
//            SecretKeySpec secretKey = new SecretKeySpec(apiKey.getBytes(StandardCharsets.UTF_8), "HmacSHA512");
//            sha512Hmac.init(secretKey);
//            byte[] hashBytes = sha512Hmac.doFinal(data.getBytes(StandardCharsets.UTF_8));
//            return Base64.getEncoder().encodeToString(hashBytes);
//        } catch (Exception e) {
//            throw new RuntimeException("Failed to generate PayWay HMAC-SHA512 hash", e);
//        }
//    }
//
//    public String getMerchantId() {
//        return merchantId;
//    }
//}

package com.example.FIN_ecommerce_API.service.serviceImpl;

import com.example.FIN_ecommerce_API.dto.response.PayWayResponseDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Map;

@Service
public class PayWayService {

    @Value("${payway.merchant-id}")
    private String merchantId;

    @Value("${payway.api-key}")
    private String apiKey;

    // Use sandbox/UAT or production ABA PayWay API URL
    @Value("${payway.api-url:https://checkout-sandbox.payway.com.kh/api/payment-gateway/v1/payments/purchase}")
    private String payWayApiUrl;

    public String generateHMAC512(String data) {
        try {
            Mac sha512Hmac = Mac.getInstance("HmacSHA512");
            SecretKeySpec secretKey = new SecretKeySpec(apiKey.getBytes(StandardCharsets.UTF_8), "HmacSHA512");
            sha512Hmac.init(secretKey);
            byte[] hashBytes = sha512Hmac.doFinal(data.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hashBytes);
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate PayWay HMAC-SHA512 hash", e);
        }
    }

    public PayWayResponseDto executePayWayPurchase(Map<String, Object> requestParams) {
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestParams, headers);

        // Call ABA PayWay direct server-to-server endpoint
        return restTemplate.postForObject(payWayApiUrl, entity, PayWayResponseDto.class);
    }

    public String getMerchantId() {
        return merchantId;
    }
}
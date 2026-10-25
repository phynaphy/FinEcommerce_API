package com.example.FIN_ecommerce_API.service.serviceImpl;

import com.example.FIN_ecommerce_API.dto.checkout.AcledaOpenSessionRequestDto;
import com.example.FIN_ecommerce_API.dto.checkout.AcledaTransactionRequestDto;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AcledaService {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final AcledaHashService hashService;

    @Value("${acleda.base-url}")
    private String baseUrl;

    @Value("${acleda.login-id}")
    private String loginId;

    @Value("${acleda.password}")
    private String password;

    @Value("${acleda.merchant-id}")
    private String merchantId;

    @Value("${acleda.secret}")
    private String secret;

    @Value("${acleda.currency:USD}")
    private String currency;

    @Value("${acleda.expiry-minutes:5}")
    private Integer expiryMinutes;

    @Value("${acleda.operation-type:5}")
    private Integer operationType;

    /**
     * Opens an ACLEDA KHQR payment session.
     *
     * This method returns ACLEDA's response as JsonNode so that
     * the caller can inspect the result before creating a payment
     * response or updating the database.
     */
    public JsonNode openSession(
            String txid,
            String invoiceId,
            BigDecimal amount,
            String description,
            String itemName,
            Integer quantity
    ) {
        if (txid == null || txid.isBlank() || txid.length() > 16) {
            throw new IllegalArgumentException(
                    "Transaction ID must contain 1 to 16 characters"
            );
        }

        if (invoiceId == null || invoiceId.isBlank()) {
            throw new IllegalArgumentException(
                    "Invoice ID is required"
            );
        }

        if (amount == null || amount.signum() <= 0) {
            throw new IllegalArgumentException(
                    "Payment amount must be greater than zero"
            );
        }

        if (quantity == null || quantity < 1) {
            throw new IllegalArgumentException(
                    "Quantity must be at least 1"
            );
        }

        String hash = hashService.generateOpenSessionHash(
                merchantId,
                loginId,
                password,
                txid,
                secret
        );

        /*
         * NOTE:
         * The bank's example sends purchaseDate as a formatted date,
         * while its parameter table says UTC timestamp.
         * Confirm the required format with ACLEDA before production.
         */
        String purchaseDate = Instant.now().toString();

        AcledaTransactionRequestDto transaction =
                AcledaTransactionRequestDto.builder()
                        .txid(txid)
                        .purchaseAmount(amount)
                        .purchaseCurrency(currency)
                        .purchaseDate(purchaseDate)
                        .purchaseDesc(description)
                        .invoiceid(invoiceId)
                        .item(itemName)
                        .quantity(quantity)
                        .expiryTime(expiryMinutes)
                        .operationType(operationType)
                        .build();

        AcledaOpenSessionRequestDto request =
                AcledaOpenSessionRequestDto.builder()
                        .loginId(loginId)
                        .password(password)
                        .merchantID(merchantId)
                        .hash(hash)
                        .xpayTransaction(transaction)
                        .build();

        String endpoint = baseUrl + "/openSessionV2";

        try {
            String requestJson = objectMapper.writeValueAsString(request);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setAccept(
                    java.util.List.of(MediaType.APPLICATION_JSON)
            );

            HttpEntity<String> entity =
                    new HttpEntity<>(requestJson, headers);

            ResponseEntity<String> response = restTemplate.exchange(
                    endpoint,
                    HttpMethod.POST,
                    entity,
                    String.class
            );

            if (!response.getStatusCode().is2xxSuccessful()
                    || response.getBody() == null) {
                throw new IllegalStateException(
                        "ACLEDA returned an unsuccessful HTTP response"
                );
            }

            JsonNode root = objectMapper.readTree(response.getBody());
            JsonNode result = root.path("result");

            if (result.isMissingNode() || result.isNull()) {
                throw new IllegalStateException(
                        "ACLEDA response does not contain result"
                );
            }

            String code = result.path("code").asText();

            if (!"0".equals(code)) {
                String details = result.path("errorDetails").asText(
                        "Unknown ACLEDA error"
                );

                throw new IllegalStateException(
                        "ACLEDA rejected the payment session: " + details
                );
            }

            String sessionId = result.path("sessionid").asText(null);
            String qrValue = result.path("qrValue").asText(null);

            if (sessionId == null || qrValue == null || qrValue.isBlank()) {
                throw new IllegalStateException(
                        "ACLEDA response is missing sessionid or qrValue"
                );
            }

            return root;

        } catch (Exception e) {
            throw new IllegalStateException(
                    "Failed to open ACLEDA KHQR session: " + e.getMessage(),
                    e
            );
        }
    }

    public String generateTransactionId() {
        // 16 uppercase hexadecimal characters.
        return UUID.randomUUID()
                .toString()
                .replace("-", "")
                .substring(0, 16)
                .toUpperCase();
    }

    public String generateInvoiceId() {
        return "INV-" + UUID.randomUUID()
                .toString()
                .replace("-", "")
                .substring(0, 12)
                .toUpperCase();
    }
}
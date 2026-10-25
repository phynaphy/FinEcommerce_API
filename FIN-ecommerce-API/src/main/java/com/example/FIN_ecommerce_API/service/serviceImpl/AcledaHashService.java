package com.example.FIN_ecommerce_API.service.serviceImpl;

import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.HexFormat;
import java.util.Locale;

@Service
public class AcledaHashService {

    public String generateHmacSHA512Hex(String message, String secret) {
        try {
            Mac mac = Mac.getInstance("HmacSHA512");

            SecretKeySpec key = new SecretKeySpec(
                    secret.getBytes(StandardCharsets.UTF_8),
                    "HmacSHA512"
            );

            mac.init(key);

            byte[] hashBytes = mac.doFinal(
                    message.getBytes(StandardCharsets.UTF_8)
            );

            return HexFormat.of()
                    .formatHex(hashBytes)
                    .toUpperCase(Locale.ROOT);

        } catch (Exception e) {
            throw new IllegalStateException(
                    "Failed to generate ACLEDA HMAC-SHA512 hash",
                    e
            );
        }
    }

    public String generateOpenSessionHash(
            String merchantId,
            String loginId,
            String password,
            String txid,
            String secret
    ) {
        // According to the ACLEDA document:
        // merchantID + loginId + password + txid
        String message = merchantId + loginId + password + txid;

        return generateHmacSHA512Hex(message, secret);
    }

    public String generateTransactionStatusHash(
            String merchantId,
            String loginId,
            String password,
            String paymentTokenId,
            String secret
    ) {
        // According to the ACLEDA document:
        // merchantId + loginId + password + paymentTokenid
        String message =
                merchantId + loginId + password + paymentTokenId;

        return generateHmacSHA512Hex(message, secret);
    }
}
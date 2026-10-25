        package com.example.FIN_ecommerce_API.dto.checkout;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AcledaPaymentResponseDto {

    private String trackingNumber;

    private String status;

    private String txid;

    private String sessionId;

    private String paymentTokenId;

    private String qrValue;

    private String qrBase64;

    private String message;
}

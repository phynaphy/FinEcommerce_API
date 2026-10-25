package com.example.FIN_ecommerce_API.dto.checkout;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AcledaTransactionRequestDto {

    private String txid;
    private BigDecimal purchaseAmount;
    private String purchaseCurrency;
    private String purchaseDate;
    private String purchaseDesc;
    private String invoiceid;
    private String item;
    private Integer quantity;
    private Integer expiryTime;
    private Integer operationType;
    private String terminalId;
    private String counterId;
}
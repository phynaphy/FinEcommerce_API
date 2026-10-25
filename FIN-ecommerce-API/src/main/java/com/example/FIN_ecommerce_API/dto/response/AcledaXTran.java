package com.example.FIN_ecommerce_API.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class AcledaXTran {
    private double purchaseAmount;
    private long purchaseDate;
    private int quantity;
    private String paymentTokenid;
    private int expiryTime;
    private long confirmDate;
    private int purchaseType;
    private double feeAmount;
    private int operationType;
}


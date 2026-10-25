package com.example.FIN_ecommerce_API.dto.request;
import lombok.Data;

@Data
public class AcledaTransactionRequest {
    private String txid;
    private String purchaseAmount;
    private String purchaseCurrency;
    private String purchaseDate;
    private String purchaseDesc;
    private String invoiceid;
    private String item;
    private String quantity;
    private String expiryTime;
    private String operationType;
}


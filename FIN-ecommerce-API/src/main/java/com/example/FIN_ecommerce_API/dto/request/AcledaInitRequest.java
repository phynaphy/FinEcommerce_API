package com.example.FIN_ecommerce_API.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class AcledaInitRequest {
    private String loginId;
    private String password;
    private String merchantID;
    private String hash;

    @JsonProperty("xpayTransaction")
    private AcledaTransactionRequest xpayTransaction;
}

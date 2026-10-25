package com.example.FIN_ecommerce_API.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class AcledaResult {
    private int TxDirection;
    private int code;
    private String errorDetails;
    private String sessionid;

    @JsonProperty("xTran")
    private AcledaXTran xTran;

    private String qrValue;
    private String qrBase64;
}

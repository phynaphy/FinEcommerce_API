package com.example.FIN_ecommerce_API.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PayWayCallbackDto {

    @JsonProperty("tran_id")
    private String tranId;

    private String status; // e.g., "0" for success or transaction status string depending on PayWay spec

    private String apv; // Authorization / Approval code

    private String hash;

    @JsonProperty("payment_option")
    private String paymentOption;

    private String amount;
}
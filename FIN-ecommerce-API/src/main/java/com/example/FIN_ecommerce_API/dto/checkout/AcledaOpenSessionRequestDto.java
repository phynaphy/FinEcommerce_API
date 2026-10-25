package com.example.FIN_ecommerce_API.dto.checkout;

import com.example.FIN_ecommerce_API.dto.checkout.AcledaTransactionRequestDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AcledaOpenSessionRequestDto {

    private String loginId;
    private String password;
    private String merchantID;
    private String hash;

    private AcledaTransactionRequestDto xpayTransaction;
}
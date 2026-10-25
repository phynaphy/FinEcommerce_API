////package com.example.FIN_ecommerce_API.dto.response;
////
////import lombok.AllArgsConstructor;
////import lombok.Builder;
////import lombok.Data;
////import lombok.NoArgsConstructor;
////
////@Data
////@NoArgsConstructor
////@AllArgsConstructor
////@Builder
////public class PayWayResponseDto {
////    private String reqTime;
////    private String merchantId;
////    private String tranId;
////    private String amount;
////    private String firstname;
////    private String lastname;
////    private String email;
////    private String phone;
////    private String type;
////    private String paymentOption;
////    private String items;
////    private String hash;
////    private String continueSuccessUrl;
////}
//
////package com.example.FIN_ecommerce_API.dto.response;
////
////import com.fasterxml.jackson.annotation.JsonProperty;
////import lombok.AllArgsConstructor;
////import lombok.Builder;
////import lombok.Data;
////import lombok.NoArgsConstructor;
////
////@Data
////@Builder
////@NoArgsConstructor
////@AllArgsConstructor
////public class PayWayResponseDto {
////
////    @JsonProperty("req_time")
////    private String reqTime;
////
////    @JsonProperty("merchant_id")
////    private String merchantId;
////
////    @JsonProperty("tran_id")
////    private String tranId;
////
////    private String amount;
////
////    private String items;
////
////    private String shipping;
////
////    private String firstname;
////
////    private String lastname;
////
////    private String email;
////
////    private String phone;
////
////    private String type;
////
////    @JsonProperty("payment_option")
////    private String paymentOption;
////
////    @JsonProperty("return_url")
////    private String returnUrl;
////
////    @JsonProperty("cancel_url")
////    private String cancelUrl;
////
////    @JsonProperty("continue_success_url")
////    private String continueSuccessUrl;
////
////    @JsonProperty("return_deeplink")
////    private String returnDeeplink;
////
////    private String currency;
////
////    @JsonProperty("custom_fields")
////    private String customFields;
////
////    @JsonProperty("return_params")
////    private String returnParams;
////
////    private String payout;
////
////    private Long lifetime;
////
////    @JsonProperty("additional_params")
////    private String additionalParams;
////
////    @JsonProperty("google_pay_token")
////    private String googlePayToken;
////
////    @JsonProperty("skip_success_page")
////    private Long skipSuccessPage;
////
////    private String hash;
////}
//
////package com.example.FIN_ecommerce_API.dto.response;
////
////import com.fasterxml.jackson.annotation.JsonProperty;
////import lombok.AllArgsConstructor;
////import lombok.Builder;
////import lombok.Data;
////import lombok.NoArgsConstructor;
////
////@Data
////@Builder
////@NoArgsConstructor
////@AllArgsConstructor
////public class PayWayResponseDto {
////
////    private StatusDto status;
////
////    @JsonProperty("qr_string")
////    private String qrString;
////
////    @JsonProperty("abapay_deeplink")
////    private String abapayDeeplink;
////
////    @JsonProperty("checkout_qr_url")
////    private String checkoutQrUrl;
////
////    @Data
////    @Builder
////    @NoArgsConstructor
////    @AllArgsConstructor
////    public static class StatusDto {
////        private String code;
////        private String message;
////
////        @JsonProperty("tran_id")
////        private String tranId;
////    }
////}
//
//package com.example.FIN_ecommerce_API.dto.response;
//
//import com.fasterxml.jackson.annotation.JsonProperty;
//import lombok.AllArgsConstructor;
//import lombok.Builder;
//import lombok.Data;
//import lombok.NoArgsConstructor;
//
//@Data
//@Builder
//@NoArgsConstructor
//@AllArgsConstructor
//public class PayWayResponseDto {
//
//    private StatusDto status;
//
//    @JsonProperty("qr_string")
//    private String qrString;
//
//    @JsonProperty("abapay_deeplink")
//    private String abapayDeeplink;
//
//    @JsonProperty("checkout_qr_url")
//    private String checkoutQrUrl;
//
//    private String amount;
//
//    @JsonProperty("merchant_name")
//    private String merchantName;
//
//    @JsonProperty("merchant_logo")
//    private String merchantLogo;
//
//    @Data
//    @Builder
//    @NoArgsConstructor
//    @AllArgsConstructor
//    public static class StatusDto {
//        private String code;
//        private String message;
//
//        @JsonProperty("tran_id")
//        private String tranId;
//    }
//}
package com.example.FIN_ecommerce_API.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class PayWayResponseDto {

    private Status status;

    @JsonProperty("qr_string")
    private String qrString;

    @JsonProperty("abapay_deeplink")
    private String abapayDeeplink;

    @JsonProperty("checkout_qr_url")
    private String checkoutQrUrl;

    private String amount;

    @JsonProperty("merchant_name")
    private String merchantName;

    @JsonProperty("merchant_logo")
    private String merchantLogo;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Status {
        private String code;
        private String message;

        @JsonProperty("tran_id")
        private String tranId;
    }
}
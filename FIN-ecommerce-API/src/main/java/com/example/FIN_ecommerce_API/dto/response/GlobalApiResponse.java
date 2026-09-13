package com.example.FIN_ecommerce_API.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GlobalApiResponse<T> {
    private int statusCode;
    private String status;
    private String message;
    private T data;

    public static <T> GlobalApiResponse<T> success(String message, T data) {
        return GlobalApiResponse.<T>builder()
                .statusCode(HttpStatus.OK.value())
                .status(HttpStatus.OK.getReasonPhrase())
                .message(message)
                .data(data)
                .build();
    }
}
package com.example.ecommerce.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data @AllArgsConstructor
public class ApiResponse<T> {
    private int code;
    private String message;
    private T data;

    public static <T> ApiResponse<T> ok(T data) { return new ApiResponse<>(200, "success", data); }
    public static ApiResponse<Void> okMessage(String msg) { return new ApiResponse<>(200, msg, null); }
}

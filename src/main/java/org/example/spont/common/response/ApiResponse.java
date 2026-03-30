package org.example.spont.common.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
public class ApiResponse<T> {

    private boolean success;
    private T data;
    private String message;

    public ApiResponse(boolean success, T data) {
        this.success = success;
        this.data = data;
    }

    public ApiResponse(boolean success, T data, String message) {
        this.success = success;
        this.data = data;
        this.message = message;
    }
}
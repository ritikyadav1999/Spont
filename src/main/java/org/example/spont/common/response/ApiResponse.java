package org.example.spont.common.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
public class ApiResponse<T> {

    private boolean success;
    private T data;

    public ApiResponse(boolean success, T data) {
        this.success = success;
        this.data = data;
    }
}
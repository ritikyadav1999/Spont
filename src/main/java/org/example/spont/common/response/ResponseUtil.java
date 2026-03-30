package org.example.spont.common.response;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class ResponseUtil {

    public static <T> ResponseEntity<ApiResponse<T>> ok(T data) {
        return ResponseEntity
                .ok(new ApiResponse<>(true, data));
    }

    public static <T> ResponseEntity<ApiResponse<T>> ok(T data,String message) {
        return ResponseEntity
                .ok(new ApiResponse<>(true, data,message));
    }

    public static <T> ResponseEntity<ApiResponse<T>> created(T data) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new ApiResponse<>(true, data));
    }



    public static <T> ResponseEntity<ApiResponse<T>> noContent() {
        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .body(new ApiResponse<>(true, null));
    }
}
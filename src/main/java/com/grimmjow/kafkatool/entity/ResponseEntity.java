package com.grimmjow.kafkatool.entity;

import lombok.Data;
import org.springframework.http.HttpStatus;

@Data
public class ResponseEntity<T> {
    private boolean success;
    private String message;
    private int code;
    private T data;

    public ResponseEntity(boolean success, String message, int code, T data) {
        this.success = success;
        this.message = message;
        this.code = code;
        this.data = data;
    }

    public static <T> ResponseEntity<T> success(T data) {
        return new ResponseEntity<>(true, "success", HttpStatus.OK.value(), data);
    }

    public static ResponseEntity<Empty> error(String message, int code) {
        return new ResponseEntity<>(false, message, code, null);
    }
}

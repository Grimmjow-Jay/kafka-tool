package com.grimmjow.kafkatool.entity;

import lombok.Data;
import org.springframework.http.HttpStatus;

@Data
public class ResponseEntity<T> {
    private boolean success;
    private String message;
    private int code;
    private T data;

    public ResponseEntity(boolean success, String message, HttpStatus httpStatus, T data) {
        this.success = success;
        this.message = message;
        this.code = httpStatus.value();
        this.data = data;
    }

    public static <T> ResponseEntity<T> success(T data) {
        return new ResponseEntity<>(true, "success", HttpStatus.OK, data);
    }

    public static <T> ResponseEntity<T> error(String message) {
        return new ResponseEntity<>(false, message, HttpStatus.OK, null);
    }
}

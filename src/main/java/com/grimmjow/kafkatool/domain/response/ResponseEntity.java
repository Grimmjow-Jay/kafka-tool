package com.grimmjow.kafkatool.domain.response;

import lombok.Data;
import org.springframework.http.HttpStatus;

/**
 * 统一返回值
 *
 * @author Grimm
 * @since 2020/5/26
 */
@Data
public class ResponseEntity<T> {

    private boolean success;
    private String message;
    private int code;
    private String reasonPhrase;
    private T data;

    public ResponseEntity(boolean success, String message, int code, String reasonPhrase, T data) {
        this.success = success;
        this.message = message;
        this.code = code;
        this.reasonPhrase = reasonPhrase;
        this.data = data;
    }

    public ResponseEntity(boolean success, String message, HttpStatus httpStatus, T data) {
        this(success, message, httpStatus.value(), httpStatus.getReasonPhrase(), data);
    }

    public static <T> ResponseEntity<T> success(T data) {
        return new ResponseEntity<>(true, "success", HttpStatus.OK, data);
    }

    public static ResponseEntity<Empty> success() {
        return new ResponseEntity<>(true, "success", HttpStatus.OK, new Empty());
    }

    public static ResponseEntity<Empty> error(String message, int code, String codeDesc) {
        return new ResponseEntity<>(false, message, code, codeDesc, null);
    }

    public static ResponseEntity<Empty> error(String message, HttpStatus httpStatus) {
        return error(message, httpStatus.value(), httpStatus.getReasonPhrase());
    }

}

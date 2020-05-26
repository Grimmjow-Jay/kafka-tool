package com.grimmjow.kafkatool.exception;

import com.grimmjow.kafkatool.entity.response.Empty;
import com.grimmjow.kafkatool.entity.response.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;

@RestController
@ControllerAdvice
public class GlobalRestExceptionHandler {

    @ExceptionHandler(value = BaseException.class)
    public ResponseEntity<Empty> handleBaseException(BaseException e) {
        return ResponseEntity.error(e.getMessage(), HttpStatus.OK.value());
    }

    @ExceptionHandler(value = Exception.class)
    public ResponseEntity<Empty> handleBaseException(Exception e) {
        return ResponseEntity.error(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value());
    }
}

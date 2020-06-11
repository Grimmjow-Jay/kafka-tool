package com.grimmjow.kafkaTool.exception;

import com.grimmjow.kafkaTool.entity.response.Empty;
import com.grimmjow.kafkaTool.entity.response.ResponseEntity;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;

/**
 * 全局异常处理
 *
 * @author Grimm
 * @since 2020/5/26
 */
@RestController
@ControllerAdvice
public class GlobalExceptionHandler {

    private static final String ERROR_PATH = "/error";

    @Bean
    public ErrorController errorController() {
        return () -> ERROR_PATH;
    }

    /**
     * 错误地址的处理，如404
     */
    @RequestMapping(ERROR_PATH)
    public ResponseEntity<Empty> errorHandler(HttpServletResponse response) {
        return ResponseEntity.error("error", response.getStatus());
    }

    /**
     * 业务已知异常
     *
     * @param e 业务异常
     */
    @ExceptionHandler(value = BaseException.class)
    public ResponseEntity<Empty> handleBaseException(BaseException e) {
        return ResponseEntity.error(e.getMessage(), HttpStatus.OK.value());
    }

    /**
     * 未知异常
     *
     * @param e 非业务异常
     */
    @ExceptionHandler(value = Exception.class)
    public ResponseEntity<Empty> handleBaseException(Exception e) {
        return ResponseEntity.error(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value());
    }
}

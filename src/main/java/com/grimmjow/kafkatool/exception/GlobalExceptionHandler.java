package com.grimmjow.kafkatool.exception;

import com.grimmjow.kafkatool.domain.response.Empty;
import com.grimmjow.kafkatool.domain.response.ResponseEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageConversionException;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 全局异常处理
 *
 * @author Grimm
 * @since 2020/5/26
 */
@RestController
@ControllerAdvice
@Slf4j
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
        return ResponseEntity.error("error", response.getStatus(), "Error Request");
    }

    /**
     * 业务已知异常
     *
     * @param e 业务已知异常
     */
    @ExceptionHandler(value = BaseException.class)
    public ResponseEntity<Empty> handleBaseException(BaseException e) {
        return ResponseEntity.error(e.getMessage(), HttpStatus.OK);
    }

    /**
     * 参数异常
     *
     * @param e 参数异常
     */
    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public ResponseEntity<Empty> handleBindException(MethodArgumentNotValidException e) {
        BindingResult bindingResult = e.getBindingResult();
        if (!bindingResult.hasErrors()) {
            return ResponseEntity.error(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
        List<ObjectError> allErrors = bindingResult.getAllErrors();
        if (CollectionUtils.isEmpty(allErrors)) {
            return ResponseEntity.error(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
        String errorMsg = allErrors
                .stream()
                .map(ObjectError::getDefaultMessage)
                .collect(Collectors.joining(", "));
        return ResponseEntity.error(errorMsg, HttpStatus.BAD_REQUEST);
    }

    /**
     * 请求参数异常
     */
    @ExceptionHandler(value = HttpMessageConversionException.class)
    public ResponseEntity<Empty> handleHttpMessageConversionException() {
        return ResponseEntity.error("请求参数异常", HttpStatus.BAD_REQUEST);
    }

    /**
     * Servlet请求异常
     */
    @ExceptionHandler(value = ServletException.class)
    public ResponseEntity<Empty> handleServletException(ServletException e) {
        return ResponseEntity.error(e.getMessage(), HttpStatus.BAD_REQUEST);
    }

    /**
     * 未知异常
     *
     * @param e 非业务异常
     */
    @ExceptionHandler(value = Exception.class)
    public ResponseEntity<Empty> handleBaseException(Exception e) {
        log.error(e.getMessage(), e);
        return ResponseEntity.error(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

}

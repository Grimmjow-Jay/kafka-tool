package com.grimmjow.kafkatool.exception;

import com.grimmjow.kafkatool.entity.ResponseEntity;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 处理http请求异常，如404
 */
@RestController
public class HttpErrorHandler implements ErrorController {

    @RequestMapping(value = "/error")
    public ResponseEntity<String> errorHandler() {
        return ResponseEntity.error("error");
    }

    @Override
    public String getErrorPath() {
        return "/error";
    }
}

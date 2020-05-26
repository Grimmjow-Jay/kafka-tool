package com.grimmjow.kafkatool.exception;

import com.grimmjow.kafkatool.entity.response.Empty;
import com.grimmjow.kafkatool.entity.response.ResponseEntity;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;

/**
 * 处理http请求异常，如404
 */
@RestController
public class HttpErrorHandler implements ErrorController {

    @RequestMapping(value = "/error")
    public ResponseEntity<Empty> errorHandler(HttpServletResponse response) {
        return ResponseEntity.error("error", response.getStatus());
    }

    @Override
    public String getErrorPath() {
        return "/error";
    }
}

package org.healthmap.openapi.exception;

import lombok.extern.slf4j.Slf4j;
import org.healthmap.openapi.error.OpenApiErrorCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class OpenApiExceptionHandler {
    @ExceptionHandler(value = OpenApiProblemException.class)
    public ResponseEntity<Object> openApiProblemException(OpenApiProblemException e) {
        OpenApiErrorCode errorCode = e.getOpenApiErrorCode();

        return ResponseEntity
                .status(errorCode.getHttpStateCode())
                .body(errorCode.getMessage());
    }
}

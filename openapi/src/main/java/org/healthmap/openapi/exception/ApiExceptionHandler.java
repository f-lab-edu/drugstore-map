package org.healthmap.openapi.exception;

import lombok.extern.slf4j.Slf4j;
import org.healthmap.openapi.error.MapApiErrorCode;
import org.healthmap.openapi.error.OpenApiErrorCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class ApiExceptionHandler {
    @ExceptionHandler(value = OpenApiProblemException.class)
    public ResponseEntity<Object> openApiProblemException(OpenApiProblemException e) {
        OpenApiErrorCode errorCode = e.getOpenApiErrorCode();
        log.error(errorCode.getMessage());

        return ResponseEntity
                .status(errorCode.getHttpStateCode())
                .body(errorCode.getMessage());
    }

    @ExceptionHandler(value = MapApiProblemException.class)
    public ResponseEntity<Object> mapApiProblemException(MapApiProblemException e) {
        MapApiErrorCode errorCode = e.getErrorCode();
        log.error(errorCode.getMessage());

        return ResponseEntity
                .status(errorCode.getErrorCode())
                .body(errorCode.getMessage());
    }
}

package org.healthmap.mapapi.exception;

import lombok.extern.slf4j.Slf4j;
import org.healthmap.mapapi.error.MapApiErrorCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class MapApiExceptionHandler {
    @ExceptionHandler(value = MapApiProblemException.class)
    public ResponseEntity<Object> mapApiProblemException(MapApiProblemException e) {
        MapApiErrorCode errorCode = e.getErrorCode();
        log.error(errorCode.getMessage());

        return ResponseEntity
                .status(errorCode.getHttpStateCode())
                .body(errorCode.getMessage());
    }
}

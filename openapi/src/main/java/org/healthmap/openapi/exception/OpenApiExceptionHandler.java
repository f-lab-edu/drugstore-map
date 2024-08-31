package org.healthmap.openapi.exception;

import lombok.extern.slf4j.Slf4j;
import org.healthmap.openapi.error.OpenApiErrorCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;

@Slf4j
@RestControllerAdvice
public class OpenApiExceptionHandler {
    @ExceptionHandler(value = OpenApiProblemException.class)
    public ResponseEntity<Object> openApiProblemException(OpenApiProblemException e) {
        Exception exception = e.getException();
        OpenApiErrorCode errorCode = OpenApiErrorCode.BAD_REQUEST;

        if (exception instanceof SAXException) {
            errorCode = OpenApiErrorCode.SAX_ERROR;
        } else if (exception instanceof IOException) {
            errorCode = OpenApiErrorCode.INPUT_OUTPUT_ERROR;
        } else if (exception instanceof ParserConfigurationException) {
            errorCode = OpenApiErrorCode.PARSER_ERROR;
        }

        return ResponseEntity
                .status(errorCode.getHttpStateCode())
                .body(errorCode.getMessage());
    }
}

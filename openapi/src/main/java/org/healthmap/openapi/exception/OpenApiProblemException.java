package org.healthmap.openapi.exception;

import lombok.Getter;
import org.healthmap.openapi.error.OpenApiErrorCode;

@Getter
public class OpenApiProblemException extends RuntimeException {
    private final OpenApiErrorCode openApiErrorCode;
    private final String errorMessage;

    public OpenApiProblemException(OpenApiErrorCode openApiErrorCode) {
        this.openApiErrorCode = openApiErrorCode;
        this.errorMessage = openApiErrorCode.getMessage();
    }

    public OpenApiProblemException(OpenApiErrorCode openApiErrorCode, String errorMessage) {
        this.openApiErrorCode = openApiErrorCode;
        this.errorMessage = errorMessage;
    }
}

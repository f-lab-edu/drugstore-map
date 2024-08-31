package org.healthmap.openapi.exception;

import lombok.Getter;

@Getter
public class OpenApiProblemException extends RuntimeException {
    private final Exception exception;

    public OpenApiProblemException(Exception exception) {
        this.exception = exception;
    }
}

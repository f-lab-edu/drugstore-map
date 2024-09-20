package org.healthmap.openapi.exception;

import lombok.Getter;
import org.healthmap.openapi.error.MapApiErrorCode;

@Getter
public class MapApiProblemException extends RuntimeException{
    private final MapApiErrorCode errorCode;
    private final String message;

    public MapApiProblemException(MapApiErrorCode errorCode) {
        this.errorCode = errorCode;
        this.message = errorCode.getMessage();
    }

    public MapApiProblemException(MapApiErrorCode errorCode, String message) {
        this.errorCode = errorCode;
        this.message = message;
    }
}

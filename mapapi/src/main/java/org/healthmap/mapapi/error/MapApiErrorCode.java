package org.healthmap.mapapi.error;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum MapApiErrorCode {
    SERVER_ERROR(500, 500, "서버 에러"),
    NULL_POINT(500, 5010, "Null Point")
    ;
    private final int httpStateCode;
    private final int errorCode;
    private final String message;
}

package org.healthmap.openapi.error;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum OpenApiErrorCode {
    BAD_REQUEST(400, 400, "잘못된 요청"),
    SERVER_ERROR(500, 500, "서버 에러"),
    INPUT_OUTPUT_ERROR(400, 4000, "잘못 전송된 입력값"),
    NULL_POINT(500, 5001, "Null Point"),
    OPEN_API_REQUEST_ERROR(500, 5002, "Open Api Request 에러"),
    TOO_MANY_TRY(500, 5003, "Too Many Try");

    private final int httpStateCode;
    private final int errorCode;
    private final String message;
}

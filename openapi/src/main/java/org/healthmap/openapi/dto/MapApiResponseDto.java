package org.healthmap.openapi.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor
public class MapApiResponseDto {
    private String id;
    private String x;
    private String y;

    public static MapApiResponseDto of(String id, String x, String y) {
        return new MapApiResponseDto(id, x, y);
    }
}

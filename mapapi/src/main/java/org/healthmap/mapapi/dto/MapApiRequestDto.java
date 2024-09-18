package org.healthmap.mapapi.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class MapApiRequestDto {
    private String id;
    private String address;

    public static MapApiRequestDto of(String id, String address) {
        return new MapApiRequestDto(id, address);
    }
}

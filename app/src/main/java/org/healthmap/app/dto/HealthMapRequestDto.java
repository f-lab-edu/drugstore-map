package org.healthmap.app.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
public class HealthMapRequestDto {
    private double latitude;
    private double longitude;

    public static HealthMapRequestDto of(double latitude, double longitude) {
        return new HealthMapRequestDto(latitude, longitude);
    }
}

package org.healthmap.app.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
public class HealthMapRequestDto {
    private double x;   // longitude
    private double y;   // latitude

    public static HealthMapRequestDto of(double x, double y) {
        return new HealthMapRequestDto(x, y);
    }
}

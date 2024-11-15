package org.healthmap.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class FacilityIdDto {
    private String id;

    public FacilityIdDto(String id) {
        this.id = id;
    }
}

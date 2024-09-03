package org.healthmap.openapi.converter;

import org.healthmap.db.medicalfacility.MedicalFacilityEntity;
import org.healthmap.openapi.dto.MedicalFacilityDto;
import org.healthmap.openapi.error.OpenApiErrorCode;
import org.healthmap.openapi.exception.OpenApiProblemException;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class MedicalFacilityConverter {
    public static MedicalFacilityEntity toEntity(MedicalFacilityDto dto) {
        return Optional.ofNullable(dto)
                .map(x -> MedicalFacilityEntity.of(
                        dto.getCode(), dto.getName(), dto.getAddress(), dto.getPhoneNumber(), dto.getType(),
                        dto.getState(), dto.getCity(), dto.getTown(), dto.getPostNumber(), dto.getCoordinate()))
                .orElseThrow(() -> new OpenApiProblemException(OpenApiErrorCode.NULL_POINT));
    }

    public static List<MedicalFacilityEntity> toEntityList (List<MedicalFacilityDto> dtoList) {
        return Optional.ofNullable(dtoList)
                .map(x -> x.stream()
                        .map(MedicalFacilityConverter::toEntity)
                        .collect(Collectors.toList())
                )
                .orElseThrow(() -> new OpenApiProblemException(OpenApiErrorCode.NULL_POINT));
    }
}

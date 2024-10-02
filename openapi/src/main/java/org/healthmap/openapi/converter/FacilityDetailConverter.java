package org.healthmap.openapi.converter;

import org.healthmap.db.medicalfacility.MedicalFacilityEntity;
import org.healthmap.openapi.dto.FacilityDetailUpdateDto;
import org.healthmap.openapi.error.OpenApiErrorCode;
import org.healthmap.openapi.exception.OpenApiProblemException;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class FacilityDetailConverter {
    public static MedicalFacilityEntity toEntity(FacilityDetailUpdateDto dto) {
        return Optional.ofNullable(dto)
                .map(x -> MedicalFacilityEntity.of(
                        dto.getCode(), null, null, null, null, null, null, null, null,
                        null, null, dto.getParking(), dto.getParkingEtc(), dto.getTreatmentMon(), dto.getTreatmentTue(),
                        dto.getTreatmentWed(), dto.getTreatmentThu(), dto.getTreatmentFri(), dto.getTreatmentSat(), dto.getTreatmentSun(),
                        dto.getReceiveWeek(), dto.getReceiveSat(), dto.getLunchWeek(), dto.getLunchSat(), dto.getNoTreatmentSun(), dto.getNoTreatmentHoliday(),
                        dto.getEmergencyDay(), dto.getEmergencyNight()
                )).orElseThrow(() -> new OpenApiProblemException(OpenApiErrorCode.NULL_POINT));
    }

    public static List<MedicalFacilityEntity> toEntityList(List<FacilityDetailUpdateDto> dtoList) {
        return Optional.ofNullable(dtoList)
                .map(x -> x.stream()
                        .map(FacilityDetailConverter::toEntity)
                        .collect(Collectors.toList())
                )
                .orElseThrow(() -> new OpenApiProblemException(OpenApiErrorCode.NULL_POINT));

    }
}

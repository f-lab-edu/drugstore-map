package org.healthmap.app.converter;

import org.healthmap.app.dto.HealthMapResponseDto;
import org.healthmap.db.medicalfacility.MedicalFacilityEntity;
import org.locationtech.jts.geom.Point;

public class HealthMapConverter {
    public static HealthMapResponseDto toHealthMapResponseDto(MedicalFacilityEntity entity, double distance) {
        Point coordinate = entity.getCoordinate();
        double longitude = coordinate.getX();
        double latitude = coordinate.getY();
        return HealthMapResponseDto.of(
                entity.getId(), entity.getName(), entity.getAddress(), entity.getPhoneNumber(), entity.getUrl(), entity.getPostNumber(),
                entity.getType(), entity.getState(), entity.getCity(), entity.getTown(), latitude, longitude, entity.getParking(),
                entity.getParkingEtc(), entity.getTreatmentMon(), entity.getTreatmentTue(), entity.getTreatmentWed(), entity.getTreatmentThu(),
                entity.getTreatmentFri(), entity.getTreatmentSat(), entity.getTreatmentSun(), entity.getReceiveWeek(), entity.getReceiveSat(),
                entity.getLunchWeek(), entity.getLunchSat(), entity.getNoTreatmentSun(), entity.getNoTreatmentHoliday(), entity.getEmergencyDay(),
                entity.getEmergencyNight(), distance
        );
    }
}

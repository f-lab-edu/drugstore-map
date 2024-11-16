package org.healthmap.app.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.healthmap.app.dto.HealthMapRequestDto;
import org.healthmap.app.dto.HealthMapResponseDto;
import org.healthmap.db.mongodb.model.MedicalFacility;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.GeoResult;
import org.springframework.data.geo.Metrics;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.NearQuery;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
@Slf4j
public class HealthMapService {
    private final MongoTemplate mongoTemplate;

    // {requestDto.distance} km 이내의 시설 찾기
    public List<HealthMapResponseDto> getNearByMedicalFacilityMongo(HealthMapRequestDto requestDto) {
        List<HealthMapResponseDto> healthMapResponseDtoList = new ArrayList<>();
        org.springframework.data.geo.Point location = new org.springframework.data.geo.Point(requestDto.getX(), requestDto.getY());
        NearQuery nearQuery = NearQuery.near(location)
                .maxDistance(new Distance(0.5, Metrics.KILOMETERS))
                .spherical(true)
                .limit(50);
        List<GeoResult<MedicalFacility>> result = mongoTemplate.geoNear(nearQuery, MedicalFacility.class).getContent();

        for (GeoResult<MedicalFacility> geoResult : result) {
            MedicalFacility content = geoResult.getContent();
            double distance = geoResult.getDistance().getValue();
            HealthMapResponseDto healthMapResponseDto = convertDocumentToDto(content, distance);
            healthMapResponseDtoList.add(healthMapResponseDto);
        }

        log.info("healthMapResponseDtoList size : {}", healthMapResponseDtoList.size());
        return healthMapResponseDtoList;
    }

    private HealthMapResponseDto convertDocumentToDto(MedicalFacility doc, double distance) {
        return HealthMapResponseDto.of(
                doc.getId(), doc.getName(), doc.getAddress(), doc.getPhoneNumber(), doc.getUrl(), doc.getType(),
                doc.getState(), doc.getCity(), doc.getTown(), doc.getPostNumber(), doc.getCoordinate().getX(),
                doc.getCoordinate().getY(), doc.getParking(), doc.getParkingEtc(), doc.getTreatmentMon(),
                doc.getTreatmentTue(), doc.getTreatmentWed(), doc.getTreatmentThu(), doc.getTreatmentFri(),
                doc.getTreatmentSat(), doc.getTreatmentSun(), doc.getReceiveWeek(), doc.getReceiveSat(),
                doc.getLunchWeek(), doc.getLunchSat(), doc.getNoTreatmentSun(), doc.getNoTreatmentHoliday(),
                doc.getEmergencyDay(), doc.getEmergencyNight(), distance
        );
    }
}

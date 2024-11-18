package org.healthmap.db.mongodb.model;

import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.locationtech.jts.geom.Point;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.data.mongodb.core.index.GeoSpatialIndexType;
import org.springframework.data.mongodb.core.index.GeoSpatialIndexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor
@Document(collection = "medicalFacility")
public class MedicalFacility {
    @Id
    private String id;
    private String name;
    private String address;
    private String phoneNumber;
    private String url;
    private String type;
    private String state;
    private String city;
    private String town;
    private String postNumber;

    @GeoSpatialIndexed(type = GeoSpatialIndexType.GEO_2DSPHERE) // 지리적 인덱스
    private GeoJsonPoint coordinate;                            // MongoDB의 GeoJSON Point 타입

    private String parking;
    private String parkingEtc;
    private String treatmentMon;
    private String treatmentTue;
    private String treatmentWed;
    private String treatmentThu;
    private String treatmentFri;
    private String treatmentSat;
    private String treatmentSun;
    private String receiveWeek;
    private String receiveSat;
    private String lunchWeek;
    private String lunchSat;
    private String noTreatmentSun;
    private String noTreatmentHoliday;
    private String emergencyDay;
    private String emergencyNight;
    @CreatedDate
    private LocalDateTime createdAt;
    @LastModifiedDate
    private LocalDateTime updatedAt;

    // 차후 제거
    public static MedicalFacility of(String id, String name, String address, String phoneNumber, String url, String type, String state, String city,
                                     String town, String postNumber, Point coordinate, String parking, String parkingEtc, String treatmentMon,
                                     String treatmentTue, String treatmentWed, String treatmentThu, String treatmentFri, String treatmentSat,
                                     String treatmentSun, String receiveWeek, String receiveSat, String lunchWeek, String lunchSat,
                                     String noTreatmentSun, String noTreatmentHoliday, String emergencyDay, String emergencyNight,
                                     LocalDateTime createdAt, LocalDateTime updatedAt
    ) {
        GeoJsonPoint newCoordinate = new GeoJsonPoint(coordinate.getX(), coordinate.getY());

        return new MedicalFacility(
                id, name, address, phoneNumber, url, type, state, city, town,
                postNumber, newCoordinate, parking, parkingEtc, treatmentMon, treatmentTue,
                treatmentWed, treatmentThu, treatmentFri, treatmentSat, treatmentSun,
                receiveWeek, receiveSat, lunchWeek, lunchSat, noTreatmentSun,
                noTreatmentHoliday, emergencyDay, emergencyNight, createdAt, updatedAt
        );
    }
    public static MedicalFacility of(String id, String name, String address, String phoneNumber, String url, String type, String state, String city,
                                     String town, String postNumber, Point coordinate, String parking, String parkingEtc, String treatmentMon,
                                     String treatmentTue, String treatmentWed, String treatmentThu, String treatmentFri, String treatmentSat,
                                     String treatmentSun, String receiveWeek, String receiveSat, String lunchWeek, String lunchSat,
                                     String noTreatmentSun, String noTreatmentHoliday, String emergencyDay, String emergencyNight)
    {
        GeoJsonPoint newCoordinate = new GeoJsonPoint(coordinate.getX(), coordinate.getY());

        return new MedicalFacility(
                id, name, address, phoneNumber, url, type, state, city, town,
                postNumber, newCoordinate, parking, parkingEtc, treatmentMon, treatmentTue,
                treatmentWed, treatmentThu, treatmentFri, treatmentSat, treatmentSun,
                receiveWeek, receiveSat, lunchWeek, lunchSat, noTreatmentSun,
                noTreatmentHoliday, emergencyDay, emergencyNight, null, null
        );
    }
}

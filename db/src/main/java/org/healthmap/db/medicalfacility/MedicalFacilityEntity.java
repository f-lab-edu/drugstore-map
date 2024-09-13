package org.healthmap.db.medicalfacility;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.locationtech.jts.geom.Point;



@Getter
@NoArgsConstructor
@AllArgsConstructor(access= AccessLevel.PROTECTED)
@Entity
@Table(name="medical_facility")
public class MedicalFacilityEntity {
    @Id
    private String id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String address;

    @Column(length = 30)
    private String phoneNumber;

    private String url;

    @Column(length = 30)
    private String type;

    @Column(length = 30)
    private String state;

    @Column(length = 30)
    private String city;

    @Column(length = 30)
    private String town;

    @Column(length = 16)
    private String postNumber;

    private Point coordinate;

    private String parking;
    private String parkingEtc;
    private String treatmentMon;    // 진료시간_월
    private String treatmentTue;
    private String treatmentWed;
    private String treatmentThu;
    private String treatmentFri;
    private String treatmentSat;
    private String treatmentSun;
    private String receiveWeek;     // 접수시간_평일
    private String receiveSat;      // 접수시간_토요일
    private String lunchWeek;       // 점심시간_평일
    private String lunchSat;        // 점심시간_토
    private String noTreatmentSun;      // 일요일 휴진
    private String noTreatmentHoliday;  // 공휴일 휴진
    private String emergencyDay;
    private String emergencyNight;


    // 사용되지 않을 경우 삭제할 예정
    public static MedicalFacilityEntity of(String id, String name, String address, String phoneNumber, String url, String type, String state, String city,
                                           String town, String postNumber, Point coordinate, String parking, String parkingEtc, String treatmentMon,
                                           String treatmentTue, String treatmentWed, String treatmentThu, String treatmentFri, String treatmentSat,
                                           String treatmentSun, String receiveWeek, String receiveSat, String lunchWeek, String lunchSat,
                                           String noTreatmentSun, String noTreatmentHoliday, String emergencyDay, String emergencyNight) {
        return new MedicalFacilityEntity(id, name, address, phoneNumber, url, type, state, city, town,
                postNumber, coordinate, parking, parkingEtc, treatmentMon, treatmentTue,
                treatmentWed, treatmentThu, treatmentFri, treatmentSat, treatmentSun,
                receiveWeek, receiveSat, lunchWeek, lunchSat, noTreatmentSun,
                noTreatmentHoliday, emergencyDay, emergencyNight
        );
    }
}

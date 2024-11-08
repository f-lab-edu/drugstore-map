package org.healthmap.db.mysql.model;

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
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "medical_facility")
public class MedicalFacilityEntity extends BaseEntity{
    @Id
    private String id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String address;

    @Column(name = "phone_number", length = 30)
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

    @Column(name = "post_number", length = 16)
    private String postNumber;

    private Point coordinate;

    private String parking;
    @Column(name = "parking_etc")
    private String parkingEtc;

    @Column(name = "treatment_mon")
    private String treatmentMon;    // 진료시간_월

    @Column(name = "treatment_tue")
    private String treatmentTue;

    @Column(name = "treatment_wed")
    private String treatmentWed;

    @Column(name = "treatment_thu")
    private String treatmentThu;

    @Column(name = "treatment_fri")
    private String treatmentFri;

    @Column(name = "treatment_sat")
    private String treatmentSat;

    @Column(name = "treatment_sun")
    private String treatmentSun;

    @Column(name = "receive_week")
    private String receiveWeek;     // 접수시간_평일

    @Column(name = "receive_sat")
    private String receiveSat;      // 접수시간_토요일

    @Column(name = "lunch_week")
    private String lunchWeek;       // 점심시간_평일

    @Column(name = "lunch_sat")
    private String lunchSat;        // 점심시간_토

    @Column(name = "no_treatment_sun")
    private String noTreatmentSun;      // 일요일 휴진

    @Column(name = "no_treatment_holiday")
    private String noTreatmentHoliday;  // 공휴일 휴진

    @Column(name = "emergency_day")
    private String emergencyDay;
    @Column(name = "emergency_night")
    private String emergencyNight;


    // 사용되지 않을 경우 삭제할 예정
    public static MedicalFacilityEntity of(String id, String name, String address, String phoneNumber, String url, String type, String state, String city,
                                           String town, String postNumber, Point coordinate, String parking, String parkingEtc, String treatmentMon,
                                           String treatmentTue, String treatmentWed, String treatmentThu, String treatmentFri, String treatmentSat,
                                           String treatmentSun, String receiveWeek, String receiveSat, String lunchWeek, String lunchSat,
                                           String noTreatmentSun, String noTreatmentHoliday, String emergencyDay, String emergencyNight) {
        return new MedicalFacilityEntity(
                id, name, address, phoneNumber, url, type, state, city, town,
                postNumber, coordinate, parking, parkingEtc, treatmentMon, treatmentTue,
                treatmentWed, treatmentThu, treatmentFri, treatmentSat, treatmentSun,
                receiveWeek, receiveSat, lunchWeek, lunchSat, noTreatmentSun,
                noTreatmentHoliday, emergencyDay, emergencyNight
        );
    }
}

package org.healthmap.db.medicalfacility;

import jakarta.persistence.Column;
import jakarta.persistence.ColumnResult;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityResult;
import jakarta.persistence.FieldResult;
import jakarta.persistence.Id;
import jakarta.persistence.SqlResultSetMapping;
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
@SqlResultSetMapping(
        name = "MedicalFacilityMapping",
        entities = @EntityResult(
                entityClass = MedicalFacilityEntity.class,
                fields = {
                        @FieldResult(name = "id", column = "id"),
                        @FieldResult(name = "name", column = "name"),
                        @FieldResult(name = "address", column = "address"),
                        @FieldResult(name = "phoneNumber", column = "phone_number"),
                        @FieldResult(name = "url", column = "url"),
                        @FieldResult(name = "type", column = "type"),
                        @FieldResult(name = "state", column = "state"),
                        @FieldResult(name = "city", column = "city"),
                        @FieldResult(name = "town", column = "town"),
                        @FieldResult(name = "postNumber", column = "post_number"),
                        @FieldResult(name = "coordinate", column = "coordinate"),
                        @FieldResult(name = "parking", column = "parking"),
                        @FieldResult(name = "parkingEtc", column = "parking_etc"),
                        @FieldResult(name = "treatmentMon", column = "treatment_mon"),
                        @FieldResult(name = "treatmentTue", column = "treatment_tue"),
                        @FieldResult(name = "treatmentWed", column = "treatment_wed"),
                        @FieldResult(name = "treatmentThu", column = "treatment_thu"),
                        @FieldResult(name = "treatmentFri", column = "treatment_fri"),
                        @FieldResult(name = "treatmentSat", column = "treatment_sat"),
                        @FieldResult(name = "treatmentSun", column = "treatment_sun"),
                        @FieldResult(name = "receiveWeek", column = "receive_week"),
                        @FieldResult(name = "receiveSat", column = "receive_sat"),
                        @FieldResult(name = "lunchWeek", column = "lunch_week"),
                        @FieldResult(name = "lunchSat", column = "lunch_sat"),
                        @FieldResult(name = "noTreatmentSun", column = "no_treatment_sun"),
                        @FieldResult(name = "noTreatmentHoliday", column = "no_treatment_holiday"),
                        @FieldResult(name = "emergencyDay", column = "emergency_day"),
                        @FieldResult(name = "emergencyNight", column = "emergency_night")
                }
        ),
        columns = {@ColumnResult(name = "distance", type = Double.class)}
)
@Table(name = "medical_facility")
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

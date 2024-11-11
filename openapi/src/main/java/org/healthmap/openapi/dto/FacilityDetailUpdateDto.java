package org.healthmap.openapi.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.locationtech.jts.geom.Point;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@ToString   // 차후 필요없게될 시 삭제
public class FacilityDetailUpdateDto {
    private String code;            // ykiho(암호 요양 기호)
    private String name;
    private String address;
    private String phoneNumber;
    private String url;
    private String type;
    private String state;
    private String city;
    private String town;
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
    private LocalDateTime createdAt;

    public FacilityDetailUpdateDto(String code, String name, String address, String phoneNumber, String url, String type,
                                   String state, String city, String town, String postNumber, Point coordinate, String parking,
                                   String parkingEtc, String treatmentMon, String treatmentTue, String treatmentWed,
                                   String treatmentThu, String treatmentFri, String treatmentSat, String treatmentSun,
                                   String receiveWeek, String receiveSat, String lunchWeek, String lunchSat, String noTreatmentSun,
                                   String noTreatmentHoliday, String emergencyDay, String emergencyNight,
                                   LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.code = code;
        this.name = name;
        this.address = address;
        this.phoneNumber = phoneNumber;
        this.url = url;
        this.type = type;
        this.state = state;
        this.city = city;
        this.town = town;
        this.postNumber = postNumber;
        this.coordinate = coordinate;
        this.parking = parking;
        this.parkingEtc = parkingEtc;
        this.treatmentMon = treatmentMon;
        this.treatmentTue = treatmentTue;
        this.treatmentWed = treatmentWed;
        this.treatmentThu = treatmentThu;
        this.treatmentFri = treatmentFri;
        this.treatmentSat = treatmentSat;
        this.treatmentSun = treatmentSun;
        this.receiveWeek = receiveWeek;
        this.receiveSat = receiveSat;
        this.lunchWeek = lunchWeek;
        this.lunchSat = lunchSat;
        this.noTreatmentSun = noTreatmentSun;
        this.noTreatmentHoliday = noTreatmentHoliday;
        this.emergencyDay = emergencyDay;
        this.emergencyNight = emergencyNight;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    private LocalDateTime updatedAt;


    public void addBasicInfo(String name, String address, String phoneNumber, String url,
                             String type, String state, String city, String town, String postNumber,
                             Point coordinate, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.name = name;
        this.address = address;
        this.phoneNumber = phoneNumber;
        this.url = url;
        this.type = type;
        this.state = state;
        this.city = city;
        this.town = town;
        this.postNumber = postNumber;
        this.coordinate = coordinate;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static FacilityDetailUpdateDto of(
            String code, String name, String address, String phoneNumber, String url, String type, String state,
            String city, String town, String postNumber, Point coordinate, String parking, String parkingEtc,
            String treatmentMon, String treatmentTue, String treatmentWed, String treatmentThu, String treatmentFri,
            String treatmentSat, String treatmentSun, String receiveWeek, String receiveSat, String lunchWeek,
            String lunchSat, String noTreatmentSun, String noTreatmentHoliday, String emergencyDay, String emergencyNight,
            LocalDateTime createdAt, LocalDateTime updatedAt
    ) {
        return new FacilityDetailUpdateDto(
                code, name, address, phoneNumber, url, type, state, city, town,
                postNumber, coordinate, parking, parkingEtc, treatmentMon, treatmentTue, treatmentWed,
                treatmentThu, treatmentFri, treatmentSat, treatmentSun, receiveWeek,
                receiveSat, lunchWeek, lunchSat, noTreatmentSun, noTreatmentHoliday,
                emergencyDay, emergencyNight, createdAt, updatedAt
        );
    }

}

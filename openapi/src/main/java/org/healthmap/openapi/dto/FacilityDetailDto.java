package org.healthmap.openapi.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@NoArgsConstructor
@ToString   // 차후 필요없게될 시 삭제
public class FacilityDetailDto {
    private String code;            // ykiho(암호 요양 기호)
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


    private FacilityDetailDto(String code, String parking, String parkingEtc, String treatmentMon, String treatmentTue, String treatmentWed, String treatmentThu, String treatmentFri, String treatmentSat, String treatmentSun, String receiveWeek, String receiveSat, String lunchWeek, String lunchSat, String noTreatmentSun, String noTreatmentHoliday, String emergencyDay, String emergencyNight) {
        this.code = code;
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
    }

    public static FacilityDetailDto of(
            String code, String parking, String parkingEtc, String treatmentMon, String treatmentTue, String treatmentWed,
            String treatmentThu, String treatmentFri, String treatmentSat, String treatmentSun, String receiveWeek,
            String receiveSat, String lunchWeek, String lunchSat, String noTreatmentSun, String noTreatmentHoliday,
            String emergencyDay, String emergencyNight
    ) {
        return new FacilityDetailDto(
                code, parking, parkingEtc, treatmentMon, treatmentTue, treatmentWed,
                treatmentThu, treatmentFri, treatmentSat, treatmentSun, receiveWeek,
                receiveSat, lunchWeek, lunchSat, noTreatmentSun,noTreatmentHoliday,
                emergencyDay, emergencyNight
        );
    }
}

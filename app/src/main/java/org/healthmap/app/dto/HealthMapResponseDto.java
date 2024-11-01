package org.healthmap.app.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;


@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor
@Getter
public class HealthMapResponseDto {
    private String code;        //암호화 요양 기호
    private String name;        //요양기관명(병원명)
    private String address;     //주소
    private String phoneNumber; //전화번호
    private String pageUrl; //병원 홈페이지

    private String type;    //종별코드명
    private String state;   //시도코드명
    private String city;    //시군구코드
    private String town;  //읍면동
    private String postNumber;  //우편번호
    private double latitude;
    private double longitude;
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
    private double distance;

    public static HealthMapResponseDto of(
            String code, String name, String address, String phoneNumber, String pageUrl, String type, String state,
            String city, String town, String postNumber, double latitude, double longitude, String parking, String parkingEtc,
            String treatmentMon, String treatmentTue, String treatmentWed, String treatmentThu, String treatmentFri, String treatmentSat,
            String treatmentSun, String receiveWeek, String receiveSat, String lunchWeek, String lunchSat, String noTreatmentSun,
            String noTreatmentHoliday, String emergencyDay, String emergencyNight, double distance) {
        return new HealthMapResponseDto(
                code, name, address, phoneNumber, pageUrl, type, state, city,
                town, postNumber, latitude, longitude, parking, parkingEtc, treatmentMon, treatmentTue, treatmentWed,
                treatmentThu, treatmentFri, treatmentSat, treatmentSun, receiveWeek, receiveSat,
                lunchWeek, lunchSat, noTreatmentSun, noTreatmentHoliday, emergencyDay, emergencyNight, distance
        );
    }

}

package org.healthmap.openapi.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@ToString
public class FacilityDetailDto {
    private String code;
    private String parkXpnsYn;
    private String parkEtc;
    private String trmtMonStart;    // 진료시간_월_시작
    private String trmtMonEnd;      // 진료시간_월_시작
    private String trmtTueStart;
    private String trmtTueEnd;
    private String trmtWedStart;
    private String trmtWedEnd;
    private String trmtThuStart;
    private String trmtThuEnd;
    private String trmtFriStart;
    private String trmtFriEnd;
    private String trmtSatStart;
    private String trmtSatEnd;
    private String trmtSunStart;
    private String trmtSunEnd;
    private String rcvWeek;     // 접수시간_평일
    private String rcvSat;      // 접수시간_토요일
    private String lunchWeek;       // 점심시간_평일
    private String lunchSat;        // 점심시간_토
    private String noTrmtSun;      // 일요일 휴진
    private String noTrmtHoli;  // 공휴일 휴진
    private String emyDayYn;            // 주간 응급실 운영 여부
    private String emyNgtYn;      // 야간 응급실 운영 여부

    public void saveCodeIntoDto(String code) {
        this.code = code;
    }
}

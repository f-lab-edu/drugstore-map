package org.healthmap.openapi.api;

import org.assertj.core.api.SoftAssertions;
import org.healthmap.openapi.dto.FacilityDetailDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class FacilityDetailInfoApiTest {
    @Autowired
    private FacilityDetailInfoApi facilityDetailInfoApi;

    @Test
    @DisplayName("시설을 조회할 때 성공한다 (월요일이 휴무 X)")
    public void getFacilityDetailInfo() {
        String test = "JDQ4MTAxMiM1MSMkMiMkMCMkMDAkMzgxMTkxIzIxIyQxIyQ1IyQ3OSQ0NjEwMDIjNzEjJDEjJDgjJDgz";
        FacilityDetailDto facilityDetailInfo = facilityDetailInfoApi.getFacilityDetailInfo(test);

        SoftAssertions assertions = new SoftAssertions();
        assertions.assertThat(facilityDetailInfo).isNotNull();
        assertions.assertThat(facilityDetailInfo.getTreatmentMon()).isNotNull();
        assertions.assertAll();
    }

    @Test
    @DisplayName("잘못된 암호 요양 기호로 조회할 때 모든 값이 null인 Dto가 반환된다")
    public void useMissingCode() {
        String test = "error";
        FacilityDetailDto facilityDetailInfo = facilityDetailInfoApi.getFacilityDetailInfo(test);

        SoftAssertions assertions = new SoftAssertions();
        assertions.assertThat(facilityDetailInfo).isNotNull();
        assertions.assertThat(facilityDetailInfo.getTreatmentMon()).isNull();
        assertions.assertThat(facilityDetailInfo.getTreatmentTue()).isNull();
        assertions.assertThat(facilityDetailInfo.getTreatmentWed()).isNull();
        assertions.assertThat(facilityDetailInfo.getTreatmentThu()).isNull();
        assertions.assertThat(facilityDetailInfo.getTreatmentFri()).isNull();
        assertions.assertThat(facilityDetailInfo.getTreatmentSat()).isNull();
        assertions.assertThat(facilityDetailInfo.getTreatmentSun()).isNull();
        assertions.assertThat(facilityDetailInfo.getEmergencyDay()).isNull();
        assertions.assertThat(facilityDetailInfo.getLunchSat()).isNull();
        assertions.assertThat(facilityDetailInfo.getLunchWeek()).isNull();
        assertions.assertThat(facilityDetailInfo.getParking()).isNull();
        assertions.assertThat(facilityDetailInfo.getParkingEtc()).isNull();
        assertions.assertThat(facilityDetailInfo.getEmergencyNight()).isNull();
        assertions.assertThat(facilityDetailInfo.getNoTreatmentHoliday()).isNull();
        assertions.assertThat(facilityDetailInfo.getNoTreatmentSun()).isNull();
        assertions.assertThat(facilityDetailInfo.getReceiveSat()).isNull();
        assertions.assertThat(facilityDetailInfo.getReceiveWeek()).isNull();
        assertions.assertAll();
    }
}

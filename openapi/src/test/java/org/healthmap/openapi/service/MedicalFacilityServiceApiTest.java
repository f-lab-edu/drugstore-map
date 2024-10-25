package org.healthmap.openapi.service;

import org.assertj.core.api.Assertions;
import org.healthmap.dto.BasicInfoDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
class MedicalFacilityServiceApiTest {
    @Autowired
    MedicalFacilityApiService medicalFacilityService;

    @Test
    @DisplayName("전체 시설 정보 가져오는지 확인")
    void testGetAllMedicalFacility() {
        List<BasicInfoDto> all = medicalFacilityService.getAllBasicInfo();
        System.out.println(all.get(2));
        Assertions.assertThat(all).isNotEmpty();
    }
}

package org.healthmap.openapi.service;

import jakarta.transaction.Transactional;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class MedicalFacilityServiceTest {
    @Autowired
    MedicalFacilityService medicalFacilityService;

    @Test
    @DisplayName("전체 기본 정보를 저장한다")
    @Transactional
    public void saveAllMedicalFacility() {
        int i =  medicalFacilityService.saveAllMedicalFacility();

        Assertions.assertThat(i).isNotEqualTo(0);
    }

}

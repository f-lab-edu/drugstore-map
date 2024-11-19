package org.healthmap.openapi.service;

import org.assertj.core.api.Assertions;
import org.healthmap.openapi.TestConfig;
import org.healthmap.openapi.dto.FacilityDetailUpdateDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = TestConfig.class)
class FacilityDetailApiServiceTest {
    @Autowired
    private FacilityDetailApiService facilityDetailService;

    @Test
    @DisplayName("세부 정보를 가져오는지 확인")
    public void getDetailUpdateDto() {
        String id = "JDQ4MTYyMiM1MSMkMSMkMCMkODkkMzgxMzUxIzExIyQxIyQzIyQ5OSQyNjEwMDIjNDEjJDEjJDQjJDgz";
        FacilityDetailUpdateDto detailDto = facilityDetailService.getFacilityDetailInfo(id);
        Assertions.assertThat(detailDto).isNotNull();
    }

}

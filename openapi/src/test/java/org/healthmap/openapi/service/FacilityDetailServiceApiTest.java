package org.healthmap.openapi.service;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class FacilityDetailServiceApiTest {
    @Autowired
    private FacilityDetailApiService facilityDetailService;

    @Test
    @DisplayName("세부 정보를 저장하는지 확인")
    public void saveFacilityDetail() {
        int updateCount = facilityDetailService.saveFacilityDetail();
        Assertions.assertThat(updateCount).isNotEqualTo(0);
    }

}

package org.healthmap.openapi.service;

import jakarta.transaction.Transactional;
import org.assertj.core.api.Assertions;
import org.healthmap.openapi.dto.FacilityDetailUpdateDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class FacilityDetailApiServiceTest {
    @Autowired
    private FacilityDetailApiService facilityDetailService;

    @Test
    @DisplayName("세부 정보를 저장하는지 확인")
    @Transactional
    public void saveFacilityDetail() {
        String id = null;
        FacilityDetailUpdateDto updateCountFuture = facilityDetailService.getFacilityDetailInfo(id);
        Assertions.assertThat(updateCountFuture).isNotNull();
    }

}

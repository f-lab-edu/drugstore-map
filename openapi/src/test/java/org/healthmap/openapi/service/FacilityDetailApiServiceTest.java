package org.healthmap.openapi.service;

import jakarta.transaction.Transactional;
import org.assertj.core.api.Assertions;
import org.healthmap.openapi.api.FacilityDetailInfoApi;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.CompletableFuture;

@SpringBootTest
class FacilityDetailApiServiceTest {
    @Autowired
    private FacilityDetailApiService facilityDetailService;
    @Autowired
    private FacilityDetailInfoApi facilityDetailInfoApi;

    @Test
    @DisplayName("세부 정보를 저장하는지 확인")
    @Transactional
    public void saveFacilityDetail() {
        CompletableFuture<Integer> updateCountFuture = facilityDetailService.saveFacilityDetail();
        Integer updateCount = updateCountFuture.join();
        Assertions.assertThat(updateCount).isNotEqualTo(0);
    }

}

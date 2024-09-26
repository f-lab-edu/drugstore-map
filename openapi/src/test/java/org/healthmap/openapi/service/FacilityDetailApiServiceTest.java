package org.healthmap.openapi.service;

import jakarta.transaction.Transactional;
import org.assertj.core.api.Assertions;
import org.healthmap.openapi.api.FacilityDetailInfoApi;
import org.healthmap.openapi.dto.FacilityDetailJsonDto;
import org.healthmap.openapi.dto.FacilityDetailUpdateDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

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
        int updateCount = facilityDetailService.saveFacilityDetail();
        Assertions.assertThat(updateCount).isNotEqualTo(0);
    }

    @Test
    void test() throws ExecutionException, InterruptedException {

        String test = "JDQ4MTYyMiM1MSMkMSMkMCMkOTkkNTgxMzUxIzUxIyQxIyQxIyQ2MiQzNjEyMjIjNjEjJDEjJDAjJDgz";
        CompletableFuture<FacilityDetailJsonDto> facilityDetailInfoAsync = facilityDetailInfoApi.getFacilityDetailInfoAsync(test);
        FacilityDetailJsonDto facilityDetailJsonDto = facilityDetailInfoAsync.get();
        System.out.println(facilityDetailJsonDto);

        FacilityDetailUpdateDto facilityDetailUpdateDto = facilityDetailService.convertJsonDto(facilityDetailJsonDto);
        System.out.println(facilityDetailUpdateDto);
    }

}

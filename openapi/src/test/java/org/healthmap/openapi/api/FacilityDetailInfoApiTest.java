package org.healthmap.openapi.api;

import org.assertj.core.api.Assertions;
import org.assertj.core.api.SoftAssertions;
import org.healthmap.openapi.dto.FacilityDetailUpdateDto;
import org.healthmap.openapi.dto.FacilityDetailJsonDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.LinkedBlockingQueue;

@SpringBootTest
class FacilityDetailInfoApiTest {
    @Autowired
    private FacilityDetailInfoApi facilityDetailInfoApi;

    @Test
    @DisplayName("시설을 조회할 때 성공한다 (월요일이 휴무 X)")
    public void getFacilityDetailInfo() {
        String test = "JDQ4MTAxMiM1MSMkMiMkMCMkMDAkMzgxMTkxIzIxIyQxIyQ1IyQ3OSQ0NjEwMDIjNzEjJDEjJDgjJDgz";
        FacilityDetailUpdateDto facilityDetailInfo = facilityDetailInfoApi.getFacilityDetailInfo(test);

        SoftAssertions assertions = new SoftAssertions();
        assertions.assertThat(facilityDetailInfo).isNotNull();
        assertions.assertThat(facilityDetailInfo.getTreatmentMon()).isNotNull();
        assertions.assertAll();
    }

    @Test
    @DisplayName("Jackson 라이브러리 동작 하는지 확인")
    void testJacksonLibrary() throws ExecutionException, InterruptedException {
        String test = "JDQ4MTYyMiM4MSMkMiMkMCMkMDAkMzgxNzAyIzExIyQxIyQzIyQwMyQyNjEyMjIjODEjJDEjJDYjJDgz";
        BlockingQueue<String> tempQueueForTest = new LinkedBlockingQueue<>();

        CompletableFuture<FacilityDetailJsonDto> facilityDetailInfoAsync = facilityDetailInfoApi.getFacilityDetailJsonDtoFromApi(test, tempQueueForTest);
        FacilityDetailJsonDto facilityDetailJsonDto = facilityDetailInfoAsync.get();
        System.out.println(facilityDetailJsonDto);
        Assertions.assertThat(facilityDetailJsonDto).isNotNull();
    }
}

package org.healthmap.openapi.api;

import org.assertj.core.api.Assertions;
import org.assertj.core.api.SoftAssertions;
import org.healthmap.openapi.TestConfig;
import org.healthmap.openapi.dto.FacilityDetailDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = TestConfig.class)
class FacilityDetailInfoApiTest {
    @Autowired
    private FacilityDetailInfoApi facilityDetailInfoApi;

    @Test
    @DisplayName("시설을 조회할 때 성공한다 (월요일이 휴무 X)")
    public void getFacilityDetailInfo() {
        String test = "JDQ4MTAxMiM1MSMkMiMkMCMkMDAkMzgxMTkxIzIxIyQxIyQ1IyQ3OSQ0NjEwMDIjNzEjJDEjJDgjJDgz";

        FacilityDetailDto facilityDetailInfo = facilityDetailInfoApi.getFacilityDetailDtoFromApi(test);

        SoftAssertions assertions = new SoftAssertions();
        assertions.assertThat(facilityDetailInfo).isNotNull();
        assertions.assertThat(facilityDetailInfo.getTrmtMonStart()).isNotNull();
        assertions.assertThat(facilityDetailInfo.getTrmtMonEnd()).isNotNull();
        assertions.assertAll();
    }

    @Test
    @DisplayName("Jackson 라이브러리 동작 하는지 확인")
    void testJacksonLibrary() {
        String test = "JDQ4MTYyMiM4MSMkMiMkMCMkMDAkMzgxNzAyIzExIyQxIyQzIyQwMyQyNjEyMjIjODEjJDEjJDYjJDgz";

        FacilityDetailDto facilityDetailJsonDto = facilityDetailInfoApi.getFacilityDetailDtoFromApi(test);
        System.out.println(facilityDetailJsonDto);
        Assertions.assertThat(facilityDetailJsonDto).isNotNull();
    }
}

package org.healthmap.api;

import org.assertj.core.api.Assertions;
import org.healthmap.openapi.api.HospitalApi;
import org.healthmap.openapi.config.KeyInfo;
import org.healthmap.openapi.config.PropertiesConfig;
import org.healthmap.openapi.dto.HospitalDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;

import java.util.List;

@WebMvcTest({HospitalApi.class, KeyInfo.class, PropertiesConfig.class})
class HospitalApiTest {
    @Autowired
    private HospitalApi hospitalApi;

    @Test
    @DisplayName("데이터 가져오는지 확인")
    public void getHospitalDtoList() throws Exception {
        List<HospitalDto> hospitalDtoList = hospitalApi.getHospitalInfo(1);

        // then
        Assertions.assertThat(hospitalDtoList).isNotEmpty();
        Assertions.assertThat(hospitalDtoList.size()).isEqualTo(1000);

    }
}

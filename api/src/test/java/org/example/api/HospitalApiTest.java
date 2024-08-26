package org.example.api;

import org.assertj.core.api.Assertions;
import org.example.config.KeyInfo;
import org.example.config.PropertiesConfig;
import org.example.dto.HospitalDto;
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

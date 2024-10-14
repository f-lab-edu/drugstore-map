package org.healthmap.openapi.api;

import org.assertj.core.api.Assertions;
import org.healthmap.openapi.config.KeyProperties;
import org.healthmap.openapi.config.PropertiesConfig;
import org.healthmap.openapi.config.UrlProperties;
import org.healthmap.openapi.dto.MedicalFacilityDto;
import org.healthmap.openapi.dto.MedicalFacilityXmlDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;

import java.util.List;

@WebMvcTest({MedicalFacilityApi.class, KeyProperties.class, UrlProperties.class, PropertiesConfig.class})
class MedicalFacilityApiTest {
    @Autowired
    private MedicalFacilityApi hospitalApi;
    @Autowired
    private UrlProperties urlProperties;

    @Test
    @DisplayName("Jackson으로 XML 변환해서 데이터 가져오기")
    void testXml() {
        List<MedicalFacilityXmlDto> dtos = hospitalApi.getMedicalFacilityInfoTest(urlProperties.getDrugstoreUrl(), 1);
        Assertions.assertThat(dtos).isNotEmpty();
    }

    @Nested
    @DisplayName("hospitalUrl을 사용할 때")
    class hospital {
        @Test
        @DisplayName("데이터 가져오는지 확인")
        public void getHospitalDtoList() {
            List<MedicalFacilityDto> hospitalDtoList = hospitalApi.getMedicalFacilityInfo(urlProperties.getHospitalUrl(), 1);

            // then
            Assertions.assertThat(hospitalDtoList).isNotEmpty();
            Assertions.assertThat(hospitalDtoList.size()).isNotEqualTo(0);
        }

        @Test
        @DisplayName("페이지 개수를 가져오는지 확인")
        public void getHospitalPageSize() {
            int pageSize = hospitalApi.getPageSize(urlProperties.getHospitalUrl());

            Assertions.assertThat(pageSize).isNotZero();
        }
    }

    @Nested
    @DisplayName("drugstoreUrl을 사용할 때")
    class drugstore {
        @Test
        @DisplayName("DrugstoreUrl으로부터 데이터 가져오는지 확인")
        public void getDrugstoreDtoList() {
            List<MedicalFacilityDto> drugstoreDtoList = hospitalApi.getMedicalFacilityInfo(urlProperties.getDrugstoreUrl(), 1);

            // then
            Assertions.assertThat(drugstoreDtoList).isNotEmpty();
            Assertions.assertThat(drugstoreDtoList.size()).isNotEqualTo(0);
        }

        @Test
        @DisplayName("페이지 개수를 가져오는지 확인")
        public void getDrugstorePageSize() {
            int pageSize = hospitalApi.getPageSize(urlProperties.getDrugstoreUrl());

            Assertions.assertThat(pageSize).isNotZero();
        }
    }


}

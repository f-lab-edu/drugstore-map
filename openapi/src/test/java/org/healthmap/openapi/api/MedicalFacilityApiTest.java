package org.healthmap.openapi.api;

import org.assertj.core.api.Assertions;
import org.healthmap.openapi.TestConfig;
import org.healthmap.openapi.config.UrlProperties;
import org.healthmap.openapi.dto.MedicalFacilityXmlDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest(classes = TestConfig.class)
class MedicalFacilityApiTest {
    @Autowired
    private MedicalFacilityApi medicalFacilityApi;
    @Autowired
    private UrlProperties urlProperties;

    Logger log = LoggerFactory.getLogger(MedicalFacilityApiTest.class);

    @Nested
    @DisplayName("hospitalUrl을 사용할 때")
    class hospital {
        @Test
        @DisplayName("데이터 가져오는지 확인")
        public void getHospitalDtoList() {
            List<MedicalFacilityXmlDto> hospitalDtoList = medicalFacilityApi.getMedicalFacilityInfoList(urlProperties.getHospitalUrl(), 1).join();
            log.info("list size : {}", hospitalDtoList.size());

            // then
            Assertions.assertThat(hospitalDtoList).isNotEmpty();
            Assertions.assertThat(hospitalDtoList.size()).isNotEqualTo(0);
        }

        @Test
        @DisplayName("페이지 개수를 가져오는지 확인")
        public void getHospitalPageSize() {
            int pageSize = medicalFacilityApi.getPageSize(urlProperties.getHospitalUrl());
            log.info("pageSize : {}", pageSize);

            Assertions.assertThat(pageSize).isNotZero();
        }
    }

    @Nested
    @DisplayName("drugstoreUrl을 사용할 때")
    class drugstore {
        @Test
        @DisplayName("DrugstoreUrl으로부터 데이터 가져오는지 확인")
        public void getDrugstoreDtoList() {
            List<MedicalFacilityXmlDto> drugstoreDtoList = medicalFacilityApi.getMedicalFacilityInfoList(urlProperties.getDrugstoreUrl(), 1).join();
            log.info("list size : {}", drugstoreDtoList.size());

            // then
            Assertions.assertThat(drugstoreDtoList).isNotEmpty();
            Assertions.assertThat(drugstoreDtoList.size()).isNotEqualTo(0);
        }

        @Test
        @DisplayName("페이지 개수를 가져오는지 확인")
        public void getDrugstorePageSize() {
            int pageSize = medicalFacilityApi.getPageSize(urlProperties.getDrugstoreUrl());
            log.info("pageSize : {}", pageSize);

            Assertions.assertThat(pageSize).isNotZero();
        }
    }
}

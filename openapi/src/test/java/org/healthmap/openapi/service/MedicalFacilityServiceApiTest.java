package org.healthmap.openapi.service;

import jakarta.transaction.Transactional;
import org.assertj.core.api.Assertions;
import org.healthmap.dto.BasicInfoDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
class MedicalFacilityServiceApiTest {
    @Autowired
    MedicalFacilityApiService medicalFacilityService;

    @Test
    @DisplayName("전체 기본 정보를 저장한다")
    @Transactional
    void saveAllMedicalFacility() {
        int i = medicalFacilityService.saveAllBasicInfo();
        Assertions.assertThat(i).isNotEqualTo(0);
    }

    @Test
    @DisplayName("없어진 병원 및 약국을 DB에서 삭제한다")
    @Transactional
    void deleteMedicalFacilityIdList() {
        int deleted = medicalFacilityService.deleteMedicalFacilityList();
        Assertions.assertThat(deleted).isNotEqualTo(0);
    }

    @Test
    @DisplayName("새로 생긴 병원 및 약국을 DB에 추가")
    @Transactional
    void addNewMedicalFacility() {
        int addCount = medicalFacilityService.addNewMedicalFacility();
        Assertions.assertThat(addCount).isNotEqualTo(0);
    }

    @Test
    @DisplayName("이미 DB에 있는 데이터를 update")
    @Transactional
    void updateAllMedicalFacility() {
        int updateCount = medicalFacilityService.updateAllMedicalFacility();
        Assertions.assertThat(updateCount).isNotEqualTo(0);
    }

    @Test
    @DisplayName("전체 시설 정보 가져오는지 확인")
    void testGetAllMedicalFacility() {
        List<BasicInfoDto> all = medicalFacilityService.getAllBasicInfo();
        System.out.println(all.get(2));
        Assertions.assertThat(all).isNotEmpty();
    }
}

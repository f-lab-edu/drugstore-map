package org.healthmap.app.service;

import org.assertj.core.api.Assertions;
import org.healthmap.app.dto.HealthMapRequestDto;
import org.healthmap.app.dto.HealthMapResponseDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
class HealthMapServiceTest {
    @Autowired
    private HealthMapService healthMapService;

    @Test
    @DisplayName("전체 의료시설 정보를 가져온다")
    void getAllFacility(){
        Logger log = LoggerFactory.getLogger(HealthMapServiceTest.class);
        List<HealthMapResponseDto> allMedicalFacility = healthMapService.getAllMedicalFacility();
        log.info("all medical facility size : {}", allMedicalFacility.size());
        Assertions.assertThat(allMedicalFacility).isNotNull();
    }

    @Test
    @DisplayName("좌표 근처의 병원, 약국을 가져온다")
    void getNearByFacility() {
        Logger log = LoggerFactory.getLogger(HealthMapServiceTest.class);
        HealthMapRequestDto healthMapRequestDto = HealthMapRequestDto.of( 126.9963104,37.4828517, 0.5);
        List<HealthMapResponseDto> nearByMedicalFacility = healthMapService.getNearByMedicalFacility(healthMapRequestDto);
        log.info("nearByMedicalFacility size : {}", nearByMedicalFacility.size());
        Assertions.assertThat(nearByMedicalFacility).isNotEmpty();
    }
}

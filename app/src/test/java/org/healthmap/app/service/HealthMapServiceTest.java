package org.healthmap.app.service;

import org.assertj.core.api.Assertions;
import org.healthmap.app.dto.HealthMapRequestDto;
import org.healthmap.app.dto.HealthMapResponseDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
class HealthMapServiceTest {
    @Autowired
    private HealthMapService healthMapService;

    @Test
    @DisplayName("좌표 근처의 병원, 약국을 가져온다")
    void test() {
        HealthMapRequestDto healthMapRequestDto = HealthMapRequestDto.of( 126.9963104,37.4828517);
        List<HealthMapResponseDto> nearByMedicalFacility = healthMapService.getNearByMedicalFacility(healthMapRequestDto);
        System.out.println(nearByMedicalFacility.size());
        Assertions.assertThat(nearByMedicalFacility).isNotEmpty();
    }
}

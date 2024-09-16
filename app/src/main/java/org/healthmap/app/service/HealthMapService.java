package org.healthmap.app.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.healthmap.app.converter.HealthMapConverter;
import org.healthmap.app.dto.HealthMapRequestDto;
import org.healthmap.app.dto.HealthMapResponseDto;
import org.healthmap.db.medicalfacility.MedicalFacilityEntity;
import org.healthmap.db.medicalfacility.MedicalFacilityRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
@Slf4j
public class HealthMapService {
    private final MedicalFacilityRepository medicalFacilityRepository;

    // 2km 이내의 시설 찾기
    public List<HealthMapResponseDto> getNearByMedicalFacility(HealthMapRequestDto requestDto) {
        int range = 2;
        List<Object[]> nearMedicalFacility = medicalFacilityRepository.findNearMedicalFacility(requestDto.getLatitude(), requestDto.getLongitude(), range);
        List<HealthMapResponseDto> healthMapResponseDtoList = Optional.ofNullable(nearMedicalFacility)
                .map(objList -> objList.stream()
                        .map(x -> {
                            MedicalFacilityEntity entity = (MedicalFacilityEntity) x[0];
                            double distance = (double) x[1];
                            return HealthMapConverter.toHealthMapResponseDto(entity, distance);
                        })
                        .collect(Collectors.toList())
                )
                .orElseThrow(() -> new NullPointerException("값이 없습니다."));
        log.info("healthMapResponseDtoList size : {}", healthMapResponseDtoList.size());
        return healthMapResponseDtoList;
    }
}

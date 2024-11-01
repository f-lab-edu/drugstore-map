package org.healthmap.app.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.geolatte.geom.Point;
import org.healthmap.app.dto.HealthMapRequestDto;
import org.healthmap.app.dto.HealthMapResponseDto;
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
                        .map(this::convertToResponseDto)
                        .collect(Collectors.toList())
                )
                .orElseThrow(() -> new NullPointerException("값이 없습니다."));
        log.info("healthMapResponseDtoList size : {}", healthMapResponseDtoList.size());
        return healthMapResponseDtoList;
    }

    private HealthMapResponseDto convertToResponseDto(Object[] x) {
        Point coordinate = (Point) x[10];
        double longitude = coordinate.getPosition().getCoordinate(0);
        double latitude = coordinate.getPosition().getCoordinate(1);

        return HealthMapResponseDto.of(
                (String) x[0], (String) x[1], (String) x[2], (String) x[3], (String) x[4], (String) x[5],
                (String) x[6], (String) x[7], (String) x[8], (String) x[9], latitude, longitude, (String) x[11],
                (String) x[12], (String) x[13], (String) x[14], (String) x[15], (String) x[16], (String) x[17],
                (String) x[18], (String) x[19], (String) x[20], (String) x[21], (String) x[22], (String) x[23],
                (String) x[24], (String) x[25], (String) x[26], (String) x[27], (double) x[28]
                );
    }
}

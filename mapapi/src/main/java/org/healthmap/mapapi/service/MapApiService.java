package org.healthmap.mapapi.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.healthmap.db.medicalfacility.MedicalFacilityEntity;
import org.healthmap.db.medicalfacility.MedicalFacilityRepository;
import org.healthmap.mapapi.api.MapApi;
import org.healthmap.mapapi.dto.MapApiRequestDto;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class MapApiService {
    private final MapApi mapApi;
    private final MedicalFacilityRepository medicalFacilityRepository;

    @Transactional
    public int fillCoordinateFromMapApi() {
        List<MedicalFacilityEntity> medicalFacilityEntityList = getListWithNullCoordinate();
        List<MapApiRequestDto> mapApiRequestDtoList = entityToAddressIdDto(medicalFacilityEntityList);
        if (!mapApiRequestDtoList.isEmpty()) {
            updateNullCoordinate(mapApiRequestDtoList);
        }
        return mapApiRequestDtoList.size();
    }

    private void updateNullCoordinate(List<MapApiRequestDto> mapApiRequestDtoList) {
        log.info("NullCoordinate size : {}", mapApiRequestDtoList.size());
        for(MapApiRequestDto dto : mapApiRequestDtoList) {
            List<Double> XYFromMapApi = mapApi.getCoordinateFromMapApi(dto.getAddress());
            Point coordinate = convertToXYPoint(XYFromMapApi);
            log.info("point: {}, address: {}", coordinate, dto.getId());
            medicalFacilityRepository.updateNullCoordinate(coordinate, dto.getId());
        }
    }

    private Point convertToXYPoint(List<Double> xyFromMapApi) {
        double x = xyFromMapApi.get(0);
        double y = xyFromMapApi.get(1);
        GeometryFactory geometryFactory = new GeometryFactory();
        Point point = geometryFactory.createPoint(new Coordinate(x, y));
        point.setSRID(4326);
        return point;
    }

    private List<MedicalFacilityEntity> getListWithNullCoordinate() {
        return medicalFacilityRepository.findByCoordinateIsNull();
    }

    private List<MapApiRequestDto> entityToAddressIdDto(List<MedicalFacilityEntity> entityList) {
        return Optional.ofNullable(entityList)
                .map(list -> list.stream()
                        .map(entity -> MapApiRequestDto.of(entity.getId(), entity.getAddress()))
                        .collect(Collectors.toList())
                )
                .orElse(Collections.emptyList());
    }
}

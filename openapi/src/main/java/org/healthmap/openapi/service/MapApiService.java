package org.healthmap.openapi.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.healthmap.db.medicalfacility.MedicalFacilityEntity;
import org.healthmap.db.medicalfacility.MedicalFacilityRepository;
import org.healthmap.openapi.api.MapApi;
import org.healthmap.openapi.dto.MapApiRequestDto;
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
        int updateSize = 0;
        List<MedicalFacilityEntity> medicalFacilityEntityList = getListWithNullCoordinate();
        List<MapApiRequestDto> mapApiRequestDtoList = entityToMapApiRequestDto(medicalFacilityEntityList);
        if (!mapApiRequestDtoList.isEmpty()) {
            updateSize = updateNullCoordinate(mapApiRequestDtoList);
        }
        return updateSize;
    }

    private int updateNullCoordinate(List<MapApiRequestDto> mapApiRequestDtoList) {
        int updateSize = 0;
        for (MapApiRequestDto dto : mapApiRequestDtoList) {
            String address = dto.getAddress().split(",")[0];
            List<Double> XYFromMapApi = mapApi.getCoordinateFromMapApi(address);
            if (!XYFromMapApi.isEmpty()) {
                Point coordinate = convertToXYPoint(XYFromMapApi);
                medicalFacilityRepository.updateNullCoordinate(coordinate, dto.getId());
                updateSize++;
            }
        }
        return updateSize;
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

    private List<MapApiRequestDto> entityToMapApiRequestDto(List<MedicalFacilityEntity> entityList) {
        return Optional.ofNullable(entityList)
                .map(list -> list.stream()
                        .map(entity -> MapApiRequestDto.of(entity.getId(), entity.getAddress()))
                        .collect(Collectors.toList())
                )
                .orElse(Collections.emptyList());
    }
}

package org.healthmap.openapi.service;

import lombok.extern.slf4j.Slf4j;
import org.healthmap.dto.BasicInfoDto;
import org.healthmap.openapi.api.MapApi;
import org.healthmap.openapi.api.RoadNameApi;
import org.healthmap.openapi.dto.MapApiRequestDto;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service

public class MapApiService {
    private final MapApi mapApi;
    private final RoadNameApi roadNameApi;
    private final GeometryFactory geometryFactory;

    public MapApiService(MapApi mapApi, RoadNameApi roadNameApi) {
        this.mapApi = mapApi;
        this.roadNameApi = roadNameApi;
        this.geometryFactory = new GeometryFactory();
    }

    public Point getCoordinate(BasicInfoDto dto) {
        MapApiRequestDto mapApiRequestDto = basicDtoToMapApiRequestDto(dto);
        return getCoordinateFromMapApi(mapApiRequestDto);
    }

    private Point getCoordinateFromMapApi(MapApiRequestDto mapApiRequestDto) {
        Point zero = geometryFactory.createPoint(new Coordinate(0, 0));

        if(mapApiRequestDto.getAddress() == null){
            return zero;
        }

        // 세부정보 삭제
        String address = mapApiRequestDto.getAddress().split(",")[0];
        Optional<Point> point = getPointFromAddress(address);
        if(point.isPresent()) {
            return point.get();
        }

        // 변경된 도로명주소가 있는지 확인
        String newAddress = roadNameApi.getNewAddressFromApi(address);
        point = getPointFromAddress(newAddress);
        if (point.isPresent()) {
            return point.get();
        }

        // 1차 처리
        address = address.split("-")[0];
        newAddress = roadNameApi.getNewAddressFromApi(address);
        point = getPointFromAddress(newAddress);
        if (point.isPresent()) {
            return point.get();
        }

        // 도로명까지만 검색
        address = removeLastPart(address);
        newAddress = roadNameApi.getNewAddressFromApi(address);
        return getPointFromAddress(newAddress).orElse(zero);
    }

    private static String removeLastPart(String address) {
        String[] splitAddress = address.split(" ");
        return address.replace(splitAddress[splitAddress.length-1], "");
    }

    private Optional<Point> getPointFromAddress(String newAddress) {
        if (newAddress != null) {
            List<Double> coordinates = mapApi.getCoordinateFromMapApi(newAddress);
            if (!coordinates.isEmpty()) {
                return Optional.of(convertToXYPoint(coordinates));
            }
        }
        return Optional.empty();
    }

    private Point convertToXYPoint(List<Double> xyFromMapApi) {
        double x = xyFromMapApi.get(0);
        double y = xyFromMapApi.get(1);

        Point point = geometryFactory.createPoint(new Coordinate(x, y));
        point.setSRID(4326);
        return point;
    }

    private MapApiRequestDto basicDtoToMapApiRequestDto(BasicInfoDto dto) {
        if (dto != null) {
            return MapApiRequestDto.of(dto.getCode(), dto.getAddress());
        } else {
            return null;
        }
    }
}

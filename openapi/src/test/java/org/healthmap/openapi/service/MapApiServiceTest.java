package org.healthmap.openapi.service;

import org.assertj.core.api.Assertions;
import org.healthmap.dto.BasicInfoDto;
import org.junit.jupiter.api.Test;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class MapApiServiceTest {
    @Autowired
    MapApiService mapApiService;

    @Test
    void getCoordinate() {
        Point point = new GeometryFactory().createPoint(new Coordinate(0, 0));
        point.setSRID(4326);
        BasicInfoDto basicInfoDto = new BasicInfoDto(
                "test", "테스트", "서울특별시 노원구 공릉로43길 1, 104호 (공릉동)", "010-1234-4567", "http://test.com",
                "13246", "테스트", "서울", "노원구", "공릉로", point);
        BasicInfoDto coordinate = mapApiService.getCoordinate(basicInfoDto);
        System.out.println(coordinate.toString());

        Assertions.assertThat(coordinate.getCoordinate()).isNotEqualTo(point);
    }
}

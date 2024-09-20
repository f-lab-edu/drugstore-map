package org.healthmap.openapi.api;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
class MapApiTest {
    @Autowired
    private MapApi mapApi;

    @Test
    @DisplayName("위도, 경도를 카카오 로컬 API에서 가져오는지 확인")
    void getCoordinateFromMapApi() {
        Logger logger = LoggerFactory.getLogger(MapApiTest.class);
        String testAddress = "경상북도 성주군 성주읍 시장길 23-10, (성주읍)";
        List<Double> coordinateFromMapApi = mapApi.getCoordinateFromMapApi(testAddress);
        if(!coordinateFromMapApi.isEmpty()) {
            logger.info("x: {}", coordinateFromMapApi.get(0));
            logger.info("y: {}", coordinateFromMapApi.get(1));
        } else {
            logger.info("찾지 못했습니다.");
        }

        Assertions.assertThat(coordinateFromMapApi.size()).isEqualTo(2);
    }
}
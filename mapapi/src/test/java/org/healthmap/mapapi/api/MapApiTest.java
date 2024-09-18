package org.healthmap.mapapi.api;

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
        String testAddress = "광주광역시 광산구 첨단월봉로 99,  (산월동)";
        List<Double> coordinateFromMapApi = mapApi.getCoordinateFromMapApi(testAddress);
        logger.info("x: {}", coordinateFromMapApi.get(0));
        logger.info("y: {}", coordinateFromMapApi.get(1));

        Assertions.assertThat(coordinateFromMapApi.size()).isEqualTo(2);
    }
}

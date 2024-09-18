package org.healthmap.mapapi.service;

import jakarta.transaction.Transactional;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class MapApiServiceTest {
    @Autowired
    MapApiService mapApiService;

    @Test
    @DisplayName("비어있는 좌표를 Map API를 통해 채우기")
    @Transactional
    void fillNullCoordinate() {
        int size = mapApiService.fillCoordinateFromMapApi();
        Assertions.assertThat(size).isNotZero();
    }
}

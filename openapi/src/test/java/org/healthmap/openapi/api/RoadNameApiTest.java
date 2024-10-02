package org.healthmap.openapi.api;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;

@SpringBootTest
class RoadNameApiTest {
    @Autowired
    private RoadNameApi roadNameApi;

    @Test
    @DisplayName("위도, 경도를 카카오 로컬 API에서 가져오는지 확인")
    void getCoordinateFromMapApi() {
        Logger logger = LoggerFactory.getLogger(MapApiTest.class);
        List<String> testAddress = new ArrayList<>();
        testAddress.add("서울특별시 노원구 공릉로43길 1, 104호 (공릉동)");
        testAddress.add("전라남도 목포시 양을로 151, (산정동)");
        testAddress.add("강원특별자치도 양구군 방산면 성곡로 1788-24503, (금악보건소)");
        testAddress.add("충청남도 아산시 탕정면 매곡중앙로 70, B동 2층 213~214호 (한들물빛도시지웰시티센트럴푸르지오2단지)");
        testAddress.add("광주광역시 북구 삼각월산길 49-43, 1,2층 (삼각동)");
        testAddress.add("전라남도 나주시 월정1길 41-201, (빛가람동)");
        testAddress.add("경상북도 성주군 성주읍 시장길 23-10, (성주읍)");
        testAddress.add("경기도 화성시 동탄대로 469-12, 2층 2002호 (오산동, 동탄역 린스트라우스)");

        for (String str : testAddress) {
            str = str.split(",")[0];
            String roadAddress = roadNameApi.getCoordinateFromMapApi(str);
            System.out.println("------");
            if(roadAddress == null) {
                str = str.split("-")[0];
                roadAddress = roadNameApi.getCoordinateFromMapApi(str);
            }
            if(roadAddress == null){
                String[] tempStr = str.split(" ");
                str = str.replace(tempStr[tempStr.length-1], "");
                System.out.println(str);
            }
            logger.info(roadAddress);
        }

    }
}

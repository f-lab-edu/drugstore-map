package org.healthmap.openapi.api;

import lombok.extern.slf4j.Slf4j;
import org.healthmap.openapi.config.KeyProperties;
import org.healthmap.openapi.config.UrlProperties;
import org.healthmap.openapi.dto.FacilityDetailDto;
import org.healthmap.openapi.error.OpenApiErrorCode;
import org.healthmap.openapi.exception.OpenApiProblemException;
import org.healthmap.openapi.pattern.PatternMatcherManager;
import org.healthmap.openapi.utility.XmlUtils;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@Component
public class FacilityDetailInfoApi {
    private final KeyProperties keyProperties;
    private final UrlProperties urlProperties;
    private final PatternMatcherManager patternMatcherManager;

    public FacilityDetailInfoApi(KeyProperties keyProperties, UrlProperties urlProperties, PatternMatcherManager patternMatcherManager) {
        this.keyProperties = keyProperties;
        this.urlProperties = urlProperties;
        this.patternMatcherManager = patternMatcherManager;
    }

    public FacilityDetailDto getFacilityDetailInfo(String code) {

        FacilityDetailDto facilityDetailDto = null;
        String url = urlProperties.getDetailUrl()
                + "?serviceKey=" + keyProperties.getServerKey()    //Service Key
                + "&ykiho=" + code;
        try {
            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
            Document parse = documentBuilder.parse(url);

            parse.getDocumentElement().normalize();
            NodeList nodeList = parse.getElementsByTagName("item");
            if (nodeList.getLength() == 0) {
                log.info("해당 암호 요양 기호로부터 받을 수 있는 내용이 없습니다 : {}", code);
                facilityDetailDto = FacilityDetailDto.ofNull();
            }

            for (int i = 0; i < nodeList.getLength(); i++) {
                Node item = nodeList.item(i);
                if (item.getNodeType() == Node.ELEMENT_NODE) {
                    Element element = (Element) item;
                    String parking = XmlUtils.getStringFromElement("parkXpnsYn", element);
                    String parkingEtc = XmlUtils.getStringFromElement("parkEtc", element);
                    String treatmentMon = XmlUtils.getTreatmentTimeFromElement("trmtMonStart", "trmtMonEnd", element);
                    String treatmentTue = XmlUtils.getTreatmentTimeFromElement("trmtTueStart", "trmtTueEnd", element);
                    String treatmentWed = XmlUtils.getTreatmentTimeFromElement("trmtWedStart", "trmtWedEnd", element);
                    String treatmentThu = XmlUtils.getTreatmentTimeFromElement("trmtThuStart", "trmtThuEnd", element);
                    String treatmentFri = XmlUtils.getTreatmentTimeFromElement("trmtFriStart", "trmtFriEnd", element);
                    String treatmentSat = XmlUtils.getTreatmentTimeFromElement("trmtSatStart", "trmtSatEnd", element);
                    String treatmentSun = XmlUtils.getTreatmentTimeFromElement("trmtSunStart", "trmtSunEnd", element);
                    String receiveWeek = XmlUtils.getStringFromElement("rcvWeek", element);
                    receiveWeek = changeTimeFormat(receiveWeek);
                    String receiveSat = XmlUtils.getStringFromElement("rcvSat", element);
                    receiveSat = changeTimeFormat(receiveSat);
                    String noTreatmentSun = XmlUtils.getStringFromElement("noTrmtSun", element);
                    noTreatmentSun = checkNoTreatment(noTreatmentSun);

                    String noTreatmentHoliday = XmlUtils.getStringFromElement("noTrmtHoli", element);
                    noTreatmentHoliday = checkNoTreatment(noTreatmentHoliday);

                    String lunchWeek = XmlUtils.getStringFromElement("lunchWeek", element);
                    lunchWeek = checkNoLunch(lunchWeek);
                    if (!lunchWeek.equals("없음")) {
                        lunchWeek = changeTimeFormat(lunchWeek);
                    }
                    String lunchSat = XmlUtils.getStringFromElement("lunchSat", element);
                    lunchSat = checkNoLunch(lunchSat);
                    if (!lunchSat.equals("없음")) {
                        lunchSat = changeTimeFormat(lunchWeek);
                    }

                    String emergencyDay = XmlUtils.getStringFromElement("emyDayYn", element);
                    String emergencyNight = XmlUtils.getStringFromElement("emyNgtYn", element);
                    facilityDetailDto = FacilityDetailDto.of(
                            parking, parkingEtc, treatmentMon, treatmentTue, treatmentWed, treatmentThu, treatmentFri, treatmentSat, treatmentSun,
                            receiveWeek, receiveSat, lunchWeek, lunchSat, noTreatmentSun, noTreatmentHoliday, emergencyDay, emergencyNight
                    );
                    System.out.println(facilityDetailDto);
                }
            }
        } catch (IOException ie) {
            throw new OpenApiProblemException(OpenApiErrorCode.INPUT_OUTPUT_ERROR);
        } catch (Exception e) {
            throw new OpenApiProblemException(OpenApiErrorCode.SERVER_ERROR);
        }
        return facilityDetailDto;
    }

    // 정상 진료: 휴진없음, 휴무 관련 : 휴무, 값이 없는 경우: null
    private String checkNoTreatment(String dateTime) {
        String treatmentY = "휴진없음";
        String treatmentN = "휴진";
        Set<String> yesTreatment = new HashSet<>(List.of("정상근무", "정규진료", "진료", "휴진없음"));
        Set<String> noTreatment = new HashSet<>(
                List.of("전부휴진", "모두휴진", "휴진", "휴무", "전부휴일", "전부휴무", "매주휴진", "종일휴진", "휴뮤", "휴진입니다.")
        );
        if (dateTime == null) {
            return null;
        }

        String withoutSpace = dateTime.replaceAll(" ", "");
        if (yesTreatment.contains(withoutSpace)) {
            return treatmentY;
        }
        if (noTreatment.contains(withoutSpace)) {
            return treatmentN;
        }
        return dateTime;
    }

    private String checkNoLunch(String lunchTime) {
        String no = "없음";
        Set<String> noLunchOrReceive = new HashSet<>(List.of("공란", "휴진", "없음", "휴무", "전체휴진", "오전진료", "무", "점심시간없음"));
        if (lunchTime == null) {
            return null;
        }

        String withoutSpace = lunchTime.replaceAll(" ", "");
        if (noLunchOrReceive.contains(withoutSpace)) {
            return no;
        } else {
            return lunchTime;
        }
    }

    private String changeTimeFormat(String input) {
        return patternMatcherManager.matchAndFormat(input);
    }
}
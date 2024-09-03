package org.healthmap.openapi.api;

import lombok.extern.slf4j.Slf4j;
import org.healthmap.openapi.config.KeyProperties;
import org.healthmap.openapi.dto.MedicalFacilityDto;
import org.healthmap.openapi.error.OpenApiErrorCode;
import org.healthmap.openapi.exception.OpenApiProblemException;
import org.healthmap.openapi.utility.XmlUtils;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

//TODO: 예외 처리
@Slf4j
@Component
public class MedicalFacilityApi {
    private final KeyProperties keyInfo;
    private final String page;          //페이지 번호
    private final int rowSize;          //한 페이지 결과 수
    private final String serviceKey;
    private final String numOfRows;

    public MedicalFacilityApi(KeyProperties keyInfo) {
        this.keyInfo = keyInfo;
        this.page = "&pageNo=";
        this.rowSize = 30000;
        this.serviceKey = "?serviceKey=" + keyInfo.getServerKey();
        this.numOfRows = "&numOfRows=" + rowSize;
    }

    /**
     * 전체 페이지 크기를 반환하는 메서드
     */
    public int getPageSize(String url) {
        int totalCount = getTotalCount(url);
        int pageSize = totalCount / rowSize + 1;
        log.info("pageSize : {}", pageSize);
        return pageSize;
    }


    /**
     * 병원 정보를 pageNo번 페이지에서 rowSize 만큼 가져오는 메서드
     */
    public List<MedicalFacilityDto> getMedicalFacilityInfo(String url, int pageNo) {
        List<MedicalFacilityDto> hospitalDtoList = new ArrayList<>();
        String realUrl = url + serviceKey + numOfRows + page + pageNo;   //실제 호출할 URL

        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document parse = db.parse(realUrl);

            parse.getDocumentElement().normalize();
            NodeList nodeList = parse.getElementsByTagName("item");

            for (int i = 0; i < nodeList.getLength(); i++) {
                Node node = nodeList.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element element = (Element) node;
                    String code = XmlUtils.getStringFromElement("ykiho", element);
                    String name = XmlUtils.getStringFromElement("yadmNm", element);
                    String address = XmlUtils.getStringFromElement("addr", element);
                    String phoneNumber = XmlUtils.getPhoneNumberFromElement("telno", element);
                    String pageUrl = getPageUrlFromElement(element);
                    String typeName = XmlUtils.getStringFromElement("clCdNm", element);
                    String postNumber = XmlUtils.getStringFromElement("postNo", element);
                    String stateName = XmlUtils.getStringFromElement("sidoCdNm", element);
                    String cityName = XmlUtils.getStringFromElement("sgguCdNm", element);
                    String emdongName = XmlUtils.getStringFromElement("emdongNm", element);
                    String xPos = XmlUtils.getStringFromElement("XPos", element);
                    String yPos = XmlUtils.getStringFromElement("YPos", element);
                    Point coordinate = getPointFromXYPos(xPos, yPos);

                    MedicalFacilityDto medicalFacilityDto = new MedicalFacilityDto(
                            code, name, address, phoneNumber, pageUrl, postNumber, typeName, stateName, cityName, emdongName, coordinate
                    );
                    hospitalDtoList.add(medicalFacilityDto);
                }
            }
        } catch (IOException ie) {
            throw new OpenApiProblemException(OpenApiErrorCode.INPUT_OUTPUT_ERROR);
        } catch (Exception e) {
            throw new OpenApiProblemException(OpenApiErrorCode.SERVER_ERROR);
        }
        return hospitalDtoList;
    }

    private Point getPointFromXYPos(String xPos, String yPos) {
        if (xPos == null || yPos == null) {
            return null;
        }
        GeometryFactory geometryFactory = new GeometryFactory();
        double x = Double.parseDouble(xPos);
        double y = Double.parseDouble(yPos);
        return geometryFactory.createPoint(new Coordinate(x, y));
    }

    //데이터 전체 개수를 반환하는 메서드
    private int getTotalCount(String url) {
        String realUrl = url + serviceKey;
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document parse = db.parse(realUrl);

            parse.getDocumentElement().normalize();
            String totalCount = parse.getElementsByTagName("totalCount").item(0).getTextContent();
            return Integer.parseInt(totalCount);
        } catch (IOException ie) {
            throw new OpenApiProblemException(OpenApiErrorCode.INPUT_OUTPUT_ERROR);
        } catch (Exception e) {
            throw new OpenApiProblemException(OpenApiErrorCode.SERVER_ERROR);
        }
    }

    // 병원 홈페이지를 추출하는 메서드
    private String getPageUrlFromElement(Element element) {
        String hospitalUrl = XmlUtils.getStringFromElement("hospUrl", element);
        if (hospitalUrl != null && hospitalUrl.equals("http://")) {
            return null;
        } else {
            return hospitalUrl;
        }
    }
}

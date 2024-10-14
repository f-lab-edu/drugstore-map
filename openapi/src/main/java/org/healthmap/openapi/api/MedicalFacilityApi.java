package org.healthmap.openapi.api;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import lombok.extern.slf4j.Slf4j;
import org.healthmap.openapi.config.KeyProperties;
import org.healthmap.openapi.dto.MedicalFacilityDto;
import org.healthmap.openapi.dto.MedicalFacilityXmlDto;
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
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
public class MedicalFacilityApi {
    private final KeyProperties keyInfo;
    private final String page;          //페이지 번호
    private final int rowSize;          //한 페이지 결과 수
    private final String serviceKey;
    private final String numOfRows;
    private final HttpClient client;
    private final ObjectMapper xmlMapper;

    public MedicalFacilityApi(KeyProperties keyInfo) {
        this.keyInfo = keyInfo;
        this.page = "&pageNo=";
        this.rowSize = 1000;   //TODO: 변경 예정
        this.serviceKey = "?serviceKey=" + keyInfo.getServerKey();
        this.numOfRows = "&numOfRows=" + rowSize;
        this.client = HttpClient.newBuilder().build();
        this.xmlMapper = new XmlMapper();
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
    //TODO: 변경 예정
    public List<MedicalFacilityDto> getMedicalFacilityInfo(String url, int pageNo) {
        List<MedicalFacilityDto> hospitalDtoList = new ArrayList<>();
        String realUrl = url + serviceKey + numOfRows + page + pageNo;   //실제 호출할 URL

        int maxRetries = 3; // 최대 재시도 횟수
        int attempt = 0;
        long waitTime = 1000;

        while (attempt < maxRetries) {
            HttpURLConnection con = null;
            try {
                URL urlObj = new URL(realUrl);
                con = (HttpURLConnection) urlObj.openConnection();
                con.setRequestMethod("GET");
                int responseCode = con.getResponseCode();
                log.info("responseCode : {}", responseCode);

                if (responseCode == HttpURLConnection.HTTP_OK) {
                    DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
                    DocumentBuilder db = dbf.newDocumentBuilder();
                    Document parse = db.parse(new InputSource(con.getInputStream()));

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
                    return hospitalDtoList;
                } else {
                    attempt++;
                    Thread.sleep(waitTime);
                }
            } catch (IOException ie) {
                throw new OpenApiProblemException(OpenApiErrorCode.INPUT_OUTPUT_ERROR);
            } catch (Exception e) {
                throw new OpenApiProblemException(OpenApiErrorCode.SERVER_ERROR);
            } finally {
                if (con != null) {
                    con.disconnect();
                }
            }
        }
        return hospitalDtoList;
    }

    public List<MedicalFacilityXmlDto> getMedicalFacilityInfoTest(String url, int pageNo) {
        List<MedicalFacilityXmlDto> hospitalDtoList = new ArrayList<>();
        String realUrl = url + serviceKey + numOfRows + page + pageNo;   //실제 호출할 URL
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(realUrl))
                .GET()
                .build();


        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            int statusCode = response.statusCode();

            if(statusCode == HttpURLConnection.HTTP_OK) {
                String body = response.body();
                JsonNode itemNode = xmlMapper.readTree(body.getBytes())
                        .get("body")
                        .get("items")
                        .get("item");
                MedicalFacilityXmlDto[] item = xmlMapper.treeToValue(itemNode, MedicalFacilityXmlDto[].class);
                log.info("items: {}", item.length);

                hospitalDtoList = Arrays.stream(item).collect(Collectors.toList());
            } else {
                throw new OpenApiProblemException(OpenApiErrorCode.OPEN_API_REQUEST_ERROR);
            }
        } catch (IOException | InterruptedException e){
            log.error(e.getMessage());
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
        Point point = geometryFactory.createPoint(new Coordinate(x, y));
        point.setSRID(4326);
        return point;
    }

    //데이터 전체 개수를 반환하는 메서드
    private int getTotalCount(String url) {
        String realUrl = url + serviceKey;
        log.info("url: {}",url);
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

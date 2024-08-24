package org.example.api;

import org.example.dto.HospitalDto;
import org.example.utility.XmlUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

//TODO: @Component
//TODO: 전체 데이터 받아오도록 변경 혹은 1000개만 받아오도록 변경 후 다른 단에서 반복문 실행
//TODO: key 숨기기
//TODO: 예외 처리

public class HospitalApi {
    private final String page = "&pageNo=";
    private final int rowSize = 1000;
    private final String url = "http://apis.data.go.kr/B551182/hospInfoServicev2/getHospBasisList"  //URL
            + "?serviceKey=서비스키"    //Service Key
            + "&numOfRows="+rowSize;    //한 페이지 결과 수

    // TODO: 예외처리
    /**
     * 전체 페이지 크기를 반환하는 메서드
     */
    public int getPageSize() throws ParserConfigurationException, IOException, SAXException {
        int totalCount = getTotalCount();
        return totalCount / rowSize + 1;
    }

    //TODO: 예외처리
    /**
     * 병원 정보를 pageNo번 페이지에서 rowSize 만큼 가져오는 메서드
     */
    public List<HospitalDto> getHospitalInfo(int pageNo) throws IOException, ParserConfigurationException, SAXException {
        List<HospitalDto> hospitalList = new ArrayList<>();
        String realUrl = url + page + pageNo;   //실제 호출할 URL

        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        Document parse = db.parse(realUrl);

        parse.getDocumentElement().normalize();
        NodeList nodeList = parse.getElementsByTagName("item");

        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element element = (Element) node;
                HospitalDto hospitalDto = HospitalDto.builder()
                        .code(XmlUtils.getStringFromElement("ykiho", element))
                        .name(XmlUtils.getStringFromElement("yadmNm", element))
                        .address(XmlUtils.getStringFromElement("addr", element))
                        .phoneNumber(XmlUtils.getPhoneNumberFromElement("telno", element))
                        .hospitalUrl(getHospitalUrlFromElement(element))
                        .postNumber(XmlUtils.getStringFromElement("postNo", element))
                        .typeName(XmlUtils.getStringFromElement("clCdNm", element))
                        .stateName(XmlUtils.getStringFromElement("sidoCdNm", element))
                        .cityName(XmlUtils.getStringFromElement("sgguCdNm", element))
                        .emdongName(XmlUtils.getStringFromElement("emdongNm", element))
                        .xPos(XmlUtils.getStringFromElement("XPos", element))
                        .yPos(XmlUtils.getStringFromElement("YPos", element))
                        .build();
                hospitalList.add(hospitalDto);
            }
        }
        return hospitalList;
    }

    //TODO: 예외 처리
    //데이터 전체 개수를 반환하는 메서드
    private int getTotalCount() throws ParserConfigurationException, IOException, SAXException {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        Document parse = db.parse(url);

        parse.getDocumentElement().normalize();
        String totalCount = parse.getElementsByTagName("totalCount").item(0).getTextContent();
        return Integer.parseInt(totalCount);
    }

    // 병원 홈페이지를 추출하는 메서드
    private String getHospitalUrlFromElement(Element element) {
        String hospitalUrl = XmlUtils.getStringFromElement("hospUrl", element);
        if (hospitalUrl != null && hospitalUrl.equals("http://")) {
            return null;
        } else {
            return hospitalUrl;
        }
    }
}

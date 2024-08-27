package org.example.api;

import jakarta.annotation.PostConstruct;
import org.example.config.KeyInfo;
import org.example.dto.DrugstoreDto;
import org.example.utility.XmlUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
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

@Component
public class DrugstoreApi {
    @Autowired
    KeyInfo keyInfo;
    private final String page = "&pageNo=";  //페이지 번호
    private final int rowSize = 1000;        //한 페이지 결과 수
    private String url;

    @PostConstruct
    public void init() {
        this.url = "http://apis.data.go.kr/B551182/pharmacyInfoService/getParmacyBasisList" /*URL*/
                + "?serviceKey=" + keyInfo.getServerKey() /*Service Key*/
                + "&numOfRows=" + rowSize;
    }

    /**
     * 전체 페이지 크기를 반환하는 메서드
     */
    public int getPageSize() throws ParserConfigurationException, SAXException, IOException {
        int totalCount = getTotalCount();
        return totalCount / rowSize + 1;
    }

    /**
     * 약국 정보를 pageNo번 페이지에서 rowSize 만큼 가져오는 메서드
     */
    public List<DrugstoreDto> getDrugstoreInfo(int pageNo) throws IOException, ParserConfigurationException, SAXException {
        List<DrugstoreDto> drugstoreDtoList = new ArrayList<>();
        String realUrl = url + page + pageNo;
        System.out.println(keyInfo.getServerKey());

        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        Document parse = db.parse(realUrl);

        parse.getDocumentElement().normalize();
        NodeList nodeList = parse.getElementsByTagName("item");

        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element element = (Element) node;
                DrugstoreDto drugstoreDto = DrugstoreDto.builder()
                        .code(XmlUtils.getStringFromElement("ykiho", element))
                        .name(XmlUtils.getStringFromElement("yadmNm", element))
                        .address(XmlUtils.getStringFromElement("addr", element))
                        .phoneNumber(XmlUtils.getPhoneNumberFromElement("telno", element))
                        .typeName(XmlUtils.getStringFromElement("clCdNm", element))
                        .postNumber(XmlUtils.getStringFromElement("postNo", element))
                        .stateName(XmlUtils.getStringFromElement("sidoCdNm", element))
                        .cityName(XmlUtils.getStringFromElement("sgguCdNm", element))
                        .emdongName(XmlUtils.getStringFromElement("emdongNm", element))
                        .xPos(XmlUtils.getStringFromElement("XPos", element))
                        .yPos(XmlUtils.getStringFromElement("YPos", element))
                        .build();
                drugstoreDtoList.add(drugstoreDto);
            }
        }
        return drugstoreDtoList;
    }

    private int getTotalCount() throws ParserConfigurationException, SAXException, IOException {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        Document parse = db.parse(url);

        parse.getDocumentElement().normalize();
        String totalCount = parse.getElementsByTagName("totalCount").item(0).getTextContent();
        return Integer.parseInt(totalCount);
    }
}

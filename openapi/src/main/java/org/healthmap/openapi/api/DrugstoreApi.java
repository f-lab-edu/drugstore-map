package org.healthmap.openapi.api;

import lombok.extern.slf4j.Slf4j;
import org.healthmap.openapi.config.KeyInfo;
import org.healthmap.openapi.dto.DrugstoreDto;
import org.healthmap.openapi.utility.XmlUtils;
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
@Slf4j
public class DrugstoreApi {
    private final KeyInfo keyInfo;
    private final String page;  //페이지 번호
    private final int rowSize;        //한 페이지 결과 수
    private String url;

    public DrugstoreApi(KeyInfo keyInfo) {
        this.keyInfo = keyInfo;
        this.page = "&pageNo=";
        this.rowSize = 1000;
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
                String typeName = XmlUtils.getStringFromElement("clCdNm", element);
                String postNumber = XmlUtils.getStringFromElement("postNo", element);
                String stateName = XmlUtils.getStringFromElement("sidoCdNm", element);
                String cityName = XmlUtils.getStringFromElement("sgguCdNm", element);
                String emdongName = XmlUtils.getStringFromElement("emdongNm", element);
                String xPos = XmlUtils.getStringFromElement("XPos", element);
                String yPos = XmlUtils.getStringFromElement("YPos", element);
                DrugstoreDto drugstoreDto = new DrugstoreDto(
                        code, name, address, phoneNumber, typeName, postNumber, stateName, cityName, emdongName, xPos, yPos
                );
                drugstoreDtoList.add(drugstoreDto);
            }
        }
        log.info("drugstoreDtoList size: {}", drugstoreDtoList.size());
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

package org.example.api;

import org.example.dto.DrugstoreDto;
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

public class DrugstoreApi {
    public static void main(String[] args) throws IOException, ParserConfigurationException, SAXException {
        String url = "http://apis.data.go.kr/B551182/pharmacyInfoService/getParmacyBasisList" /*URL*/
                + "?serviceKey=서비스키" /*Service Key*/
                + "&numOfRows=100" /*한 페이지 결과 수*/
                + "&pageNo=1"; /*페이지 번호*/


        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        Document parse = db.parse(url);

        parse.getDocumentElement().normalize();
        String totalCount = parse.getElementsByTagName("totalCount").item(0).getTextContent();  //전체 개수 반환

        NodeList nodeList = parse.getElementsByTagName("item");

        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element element = (Element) node;
                DrugstoreDto drugstoreDto = DrugstoreDto.builder()
                        .code(XmlUtils.getStringFromElement("ykiho",element))
                        .name(XmlUtils.getStringFromElement("yadmNm",element))
                        .address(XmlUtils.getStringFromElement("addr",element))
                        .phoneNumber(XmlUtils.getPhoneNumberFromElement("telno",element))
                        .typeName(XmlUtils.getStringFromElement("clCdNm",element))
                        .postNumber(XmlUtils.getStringFromElement("postNo",element))
                        .stateName(XmlUtils.getStringFromElement("sidoCdNm",element))
                        .cityName(XmlUtils.getStringFromElement("sgguCdNm",element))
                        .emdongName(XmlUtils.getStringFromElement("emdongNm",element))
                        .xPos(XmlUtils.getStringFromElement("XPos", element))
                        .yPos(XmlUtils.getStringFromElement("YPos", element))
                        .build();

                System.out.println(drugstoreDto);
            }
        }
    }
}

package org.example.api;

import org.example.dto.DrugstoreDto;
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
                + "&pageNo=1" /*페이지 번호*/
                + "&numOfRows=10"; /*한 페이지 결과 수*/

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
                            .code(element.getElementsByTagName("yadmNm").item(0).getTextContent())
                            .name(element.getElementsByTagName("ykiho").item(0).getTextContent())
                            .address(element.getElementsByTagName("addr").item(0).getTextContent())
                            .phoneNumber(element.getElementsByTagName("telno").item(0).getTextContent())
                            .typeName(element.getElementsByTagName("clCdNm").item(0).getTextContent())
                            .postNumber(element.getElementsByTagName("postNo").item(0).getTextContent())
                            .stateName(element.getElementsByTagName("sidoCdNm").item(0).getTextContent())
                            .cityName(element.getElementsByTagName("sgguCdNm").item(0).getTextContent())
                            .emdongName(element.getElementsByTagName("emdongNm").item(0).getTextContent())
                            .xPos(getStringFromElement("XPos", element))
                            .yPos(getStringFromElement("YPos", element))
                            .build();

                System.out.println(drugstoreDto);
            }
        }
    }
    public static String getStringFromElement(String tagName, Element element) {
        Node item = element.getElementsByTagName(tagName).item(0);

        if(item == null) {
            return null;
        }
        return element.getElementsByTagName(tagName).item(0).getTextContent();
    }
}

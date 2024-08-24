package org.example.utility;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

import java.util.regex.Pattern;

public class XmlUtils {
    static final String PHONE_NUMBER_PATTERN = "^\\d{2,3}-\\d{3,4}-\\d{4}$";

    /**
     * element에서 tag 이름에 해당하는 string 추출 (XML)
     * @return 값이 있으면 string, 없으면 null
     */
    public static String getStringFromElement(String tagName, Element element) {
        Node item = element.getElementsByTagName(tagName).item(0);
        if (item == null)
            return null;

        return element.getElementsByTagName(tagName).item(0).getTextContent();
    }

    /**
     * element에서 전화번호에 해당하는 string 추출
     * @return phoneNumber 반환, 일반전화 형식과 다르다면 null 반환
     */
    public static String getPhoneNumberFromElement(Element element) {
        Pattern pattern = Pattern.compile(PHONE_NUMBER_PATTERN);
        String phoneNumber = getStringFromElement("telno", element);
        if(phoneNumber != null && pattern.matcher(phoneNumber).matches()){
            return phoneNumber;
        } else {
            return null;
        }
    }
}

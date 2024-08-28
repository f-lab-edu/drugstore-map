package org.healthmap.openapi.utility;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

import java.util.regex.Pattern;

public class XmlUtils {
    static final String PHONE_NUMBER_PATTERN_1 = "^\\d{2,3}-\\d{3,4}-\\d{4}$";
    static final String PHONE_NUMBER_PATTERN_2 = "^\\d{3,4}-\\d{4}$";

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
     * @return phoneNumber, 일반전화 형식과 다르다면 null 반환
     */
    public static String getPhoneNumberFromElement(String phoneNumberTag, Element element) {
        Pattern pattern1 = Pattern.compile(PHONE_NUMBER_PATTERN_1);
        Pattern pattern2 = Pattern.compile(PHONE_NUMBER_PATTERN_2);
        String phoneNumber = getStringFromElement(phoneNumberTag, element);

        if (phoneNumber != null) {
            if (pattern1.matcher(phoneNumber).matches() || pattern2.matcher(phoneNumber).matches()) {
                return phoneNumber;
            } else {
                return null;
            }
        } else {
            return null;
        }
    }
}

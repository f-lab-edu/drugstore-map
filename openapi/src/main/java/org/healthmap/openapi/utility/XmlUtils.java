package org.healthmap.openapi.utility;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class XmlUtils {
    /**
     * element에서 tag 이름에 해당하는 string 추출 (XML)
     * @return 값이 있으면 string, 없으면 null
     */
    public static String getStringFromElement(String tagName, Element element) {
        Node item = element.getElementsByTagName(tagName).item(0);
        if (item == null)
            return null;

        return item.getTextContent();
    }

    public static String getTreatmentTimeFromElement(String startTimeTag, String EndTimeTag, Element element) {
        String startTime = getStringFromElement(startTimeTag, element);
        if (startTime == null || startTime.length() != 4) {
            return null;
        }
        String endTime = getStringFromElement(EndTimeTag, element);
        if (endTime == null || endTime.length() != 4) {
            return null;
        }
        String formattedStartTime = String.format("%s:%s", startTime.substring(0, 2), startTime.substring(2, 4));
        String formattedEndTime = String.format("%s:%s", endTime.substring(0, 2), endTime.substring(2, 4));

        return String.format("%s ~ %s", formattedStartTime, formattedEndTime);
    }
}

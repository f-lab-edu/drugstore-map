package org.healthmap.openapi.api;

import org.healthmap.openapi.dto.FacilityDetailDto;
import org.healthmap.openapi.utility.XmlUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FacilityDetailInfoApi {
    public static void main(String[] args) throws IOException, SAXException, ParserConfigurationException {
        String url = "http://apis.data.go.kr/B551182/MadmDtlInfoService2.7/getDtlInfo2.7"
                + "?serviceKey=" + "서비스키"    //Service Key
                + "&ykiho=암호요양기호";

        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
        Document parse = documentBuilder.parse(url);

        parse.getDocumentElement().normalize();
        NodeList nodeList = parse.getElementsByTagName("item");

        for (int i = 0; i < nodeList.getLength(); i++) {
            Node item = nodeList.item(i);
            if (item.getNodeType() == Node.ELEMENT_NODE) {
                Element element = (Element) item;
                String parking = XmlUtils.getStringFromElement("parkXpnsYn", element);
                String parkingEtc = XmlUtils.getStringFromElement("parkEtc", element);
                String treatmentMon = XmlUtils.getTreatmentTimeFromElement("trmtMonStart", "trmtMonEnd", element);
                String treatmentTue = XmlUtils.getTreatmentTimeFromElement("trmtTueStart", "trmtTueEnd", element);
                String treatmentWed = XmlUtils.getTreatmentTimeFromElement("trmtWedStart", "trmtWedEnd", element);
                String treatmentThu = XmlUtils.getTreatmentTimeFromElement("trmtThuStart", "trmtThuEnd", element);
                String treatmentFri = XmlUtils.getTreatmentTimeFromElement("trmtFriStart", "trmtFriEnd", element);
                String treatmentSat = XmlUtils.getTreatmentTimeFromElement("trmtSatStart", "trmtSatEnd", element);
                String treatmentSun = XmlUtils.getTreatmentTimeFromElement("trmtSunStart", "trmtSunEnd", element);
                String receiveWeek = XmlUtils.getStringFromElement("rcvWeek", element);
                receiveWeek = changeTimeFormat(receiveWeek);
                String receiveSat = XmlUtils.getStringFromElement("rcvSat", element);
                receiveSat = changeTimeFormat(receiveSat);
                String lunchWeek = XmlUtils.getStringFromElement("lunchWeek", element);
                lunchWeek = changeTimeFormat(lunchWeek);
                String lunchSat = XmlUtils.getStringFromElement("lunchSat", element);
                lunchSat = changeTimeFormat(lunchSat);
                String noTreatmentSun = XmlUtils.getStringFromElement("noTrmtSun", element);
                noTreatmentSun = checkNoTreatment(noTreatmentSun);

                String noTreatmentHoliday = XmlUtils.getStringFromElement("noTrmtHoli", element);
                noTreatmentHoliday = checkNoTreatment(noTreatmentHoliday);

                String emergencyDay = XmlUtils.getStringFromElement("emyDayYn", element);
                String emergencyNight = XmlUtils.getStringFromElement("emyNgtYn", element);
                FacilityDetailDto facilityDetailDto = FacilityDetailDto.of(
                        parking, parkingEtc, treatmentMon, treatmentTue, treatmentWed, treatmentThu, treatmentFri, treatmentSat, treatmentSun,
                        receiveWeek, receiveSat, lunchWeek, lunchSat, noTreatmentSun, noTreatmentHoliday, emergencyDay, emergencyNight
                );
                System.out.println(facilityDetailDto);
            }
        }
    }

    // 정상 진료: 휴진없음, 휴무 관련 : 휴무, 값이 없는 경우: null
    public static String checkNoTreatment(String dateTime) {
        String treatmentY = "휴진없음";
        String treatmentN = "휴진";
        Set<String> yesTreatment = new HashSet<>(List.of("정상근무", "정규진료", "진료", "휴진없음"));
        Set<String> noTreatment = new HashSet<>(
                List.of("전부휴진", "모두휴진", "휴진", "휴무", "전부휴일", "전부휴무", "매주휴진", "종일휴진", "휴뮤", "휴진입니다.")
        );
        if (dateTime == null) {
            return null;
        }

        String withoutSpace = dateTime.replaceAll(" ", "");
        if (yesTreatment.contains(withoutSpace)) {
            return treatmentY;
        }
        if (noTreatment.contains(withoutSpace)) {
            return treatmentN;
        }
        return dateTime;
    }

    public static String changeTimeFormat(String time) {
        String no = "없음";
        Set<String> noLunchOrReceive = new HashSet<>(List.of("공란", "휴진", "없음", "휴무", "전체휴진", "오전진료", "무", "점심시간없음"));
        Pattern timePattern1 = Pattern.compile("(오전|오후|아침)?(\\d{1,2})시(\\d{2}분?)?([-~])(오후|저녁)?(\\d{1,2})시(\\d{2}분?)?");      //13시(00)(분)-14시(00)(분)
        Pattern timePattern2 = Pattern.compile("(오전|오후|아침)?(\\d{1,2})([:;])(\\d{2})([-~])(오후|저녁)?(\\d{1,2})([:;])(\\d{2})");     //13(:|;)00(-|~)14(:|;)00, 1:00~2:00
        Pattern timePattern3 = Pattern.compile("(\\d{1,2})시?([-~])(\\d{1,2})시?");                           //ex) 13(시)-14(시), 01시-02시 등
        Pattern timePattern4 = Pattern.compile("(오전|오후|아침)?(\\d{1,2})시(\\d{2}분)?부터(오후|저녁)?(\\d{1,2})시(\\d{2}분)?(까지)?");         //1시부터 2시(30분)까지
        Pattern timePattern5 = Pattern.compile("(오전|오후|아침)?(\\d{1,2})시(\\d{2}분)?부터(접수)?");         //09시30분부터
        Pattern timePattern6 = Pattern.compile("(오전|오후|아침)?(\\d{1,2})[:;](\\d{2})부터(접수)?");         //09:30부터


        if (time == null) {
            return null;
        }

        String timeCheck = time.replaceAll(" ", "");
        if(noLunchOrReceive.contains(timeCheck)){    // 없는 경우
            return no;
        }

        Matcher matcher = timePattern1.matcher(timeCheck);
        if (matcher.find()) {
            StringBuilder resultStr = new StringBuilder();
            String startTimeAmPm = matcher.group(1);
            String startTime = matcher.group(2);
            String startMinute = matcher.group(3);
            String endTime = matcher.group(6);
            String endMinute = matcher.group(7);

            if(startTimeAmPm != null) {
                if(startTimeAmPm.equals("오전") || startTimeAmPm.equals("아침")){
                    resultStr.append(String.format("%02d", Integer.parseInt(startTime)));
                } else {        //오후
                    if(startTime.length() == 1) {   // 1자리
                        resultStr.append(String.format("%02d", Integer.parseInt(startTime) + 12));
                    } else {                        // 2자리
                        if(startTime.charAt(0) == '0') {        //ex) 01시
                            resultStr.append(String.format("%02d", Integer.parseInt(startTime)+12));
                        } else if(startTime.charAt(0) == '1') { //ex) 13시
                            resultStr.append(startTime);
                        }
                    }
                }
            } else {
                if(startTime.length() == 1) {
                    if (Integer.parseInt(startTime) > 5) {      //ex) 9시
                        resultStr.append(String.format("%02d", Integer.parseInt(startTime)));
                    } else {                                    //ex) 1시
                        resultStr.append((Integer.parseInt(startTime) + 12));
                    }
                } else {
                    if (startTime.charAt(0) == '0') {
                        if (Integer.parseInt(startTime) > 5) {  //ex) 09시
                            resultStr.append(startTime);
                        } else {                                //ex) 01시
                            resultStr.append((Integer.parseInt(startTime) + 12));
                        }
                    } else if(startTime.charAt(0) == '1') {     //ex) 13시
                        resultStr.append(startTime);
                    }
                }
            }
            // 시작 minute
            if (startMinute != null) {
                resultStr.append(":").append(startMinute.replace("분", "")).append(" - ");
            } else {
                resultStr.append(":00").append(" - ");
            }
            // 종료 hour
            if (endTime.length() == 1) {
                resultStr.append((Integer.parseInt(endTime) + 12));
            } else {
                if (endTime.charAt(0) == '0') {     // 두번째 hour
                    resultStr.append((Integer.parseInt(endTime) + 12));
                } else if(endTime.charAt(0) == '1'){
                    resultStr.append(endTime);
                }
            }
            // 종료 minute
            if (endMinute != null) {
                resultStr.append(":").append(endMinute.replace("분", ""));
            } else {
                resultStr.append(":00");
            }

            System.out.println("timePattern1 : " + resultStr.toString());
            return resultStr.toString();
        }

        Matcher matcher2 = timePattern2.matcher(timeCheck);            // ok
        if (matcher2.find()) {
            StringBuilder resultStr = new StringBuilder();
            String startTimeAmPm = matcher2.group(1);
            String startTime = matcher2.group(2);
            String startMinute = matcher2.group(4);
            String endTime = matcher2.group(7);
            String endMinute = matcher2.group(9);

            if(startTimeAmPm != null) {
                if(startTimeAmPm.equals("오전") || startTimeAmPm.equals("아침")){
                    resultStr.append(String.format("%02d", Integer.parseInt(startTime)));
                } else {        //오후
                    if(startTime.length() == 1) {   // 1자리
                        resultStr.append(String.format("%02d", Integer.parseInt(startTime) + 12));
                    } else {                        // 2자리
                        if(startTime.charAt(0) == '0') {        //ex) 01시
                            resultStr.append(String.format("%02d", Integer.parseInt(startTime)+12));
                        } else if(startTime.charAt(0) == '1') { //ex) 13시
                            resultStr.append(startTime);
                        }
                    }
                }
            } else {
                if(startTime.length() == 1) {
                    if (Integer.parseInt(startTime) > 5) {      //ex) 9시
                        resultStr.append(String.format("%02d", Integer.parseInt(startTime)));
                    } else {                                    //ex) 1시
                        resultStr.append((Integer.parseInt(startTime) + 12));
                    }
                } else {
                    if (startTime.charAt(0) == '0') {
                        if (Integer.parseInt(startTime) > 5) {  //ex) 09시
                            resultStr.append(startTime);
                        } else {                                //ex) 01시
                            resultStr.append((Integer.parseInt(startTime) + 12));
                        }
                    } else if(startTime.charAt(0) == '1') {     //ex) 13시
                        resultStr.append(startTime);
                    }
                }
            }
            resultStr.append(":").append(startMinute.replace("분","")).append(" - ");

            if(endTime.length() == 1) {
                resultStr.append((Integer.parseInt(endTime) + 12));
            } else {
                if (endTime.charAt(0) == '0') {
                    resultStr.append(Integer.parseInt(endTime) + 12);
                } else if(endTime.charAt(0) == '1') {
                    resultStr.append(endTime);
                }
            }
            resultStr.append(":").append(endMinute);

            System.out.println("timePattern2 : " + resultStr.toString());
            return resultStr.toString();
        }

        Matcher matcher3 = timePattern3.matcher(timeCheck);            // ok
        if (matcher3.find()) {
            StringBuilder resultStr = new StringBuilder();
            String startTime = matcher3.group(1);
            String endTime = matcher3.group(3);

            if(startTime.length() == 1) {           // 1자리
                if (Integer.parseInt(startTime) > 5) {      // 9:00
                    resultStr.append(String.format("%02d:00 - ", Integer.parseInt(startTime)));
                } else {                                    // 1:00
                    resultStr.append(String.format("%02d:00 - ", Integer.parseInt(startTime) + 12));
                }
            } else {                                // 2자리
                if (startTime.charAt(0) == '0') {
                    if (Integer.parseInt(startTime) > 5) {  // 09:00
                        resultStr.append(String.format("%02d:00 - ", Integer.parseInt(startTime)));
                    } else {                                // 01:00
                        resultStr.append(String.format("%02d:00 - ", Integer.parseInt(startTime) + 12));
                    }
                } else if (startTime.charAt(0) == '1'){                                    // 13:00
                    resultStr.append(String.format("%02d:00 - ", Integer.parseInt(startTime)));
                }
            }

            if(endTime.length() == 1) {                     // 3:00
                resultStr.append(String.format("%02d:00", Integer.parseInt(endTime) + 12));
            } else {
                if (endTime.charAt(0) == '0') {             // 03:00
                    resultStr.append(String.format("%02d:00", Integer.parseInt(endTime) + 12));
                } else if (endTime.charAt(0) == '1'){       // 11:00, 17:00
                    resultStr.append(String.format("%02d:00", Integer.parseInt(endTime)));
                }
            }

            System.out.println("timePattern3 : " + resultStr);
            return resultStr.toString();
        }

        Matcher matcher4 = timePattern4.matcher(timeCheck);            // ok
        if (matcher4.find()) {
            StringBuilder resultStr = new StringBuilder();
            String startTimeAmPm = matcher4.group(1);
            String startTime = matcher4.group(2);
            String startMinute = matcher4.group(3);
            String endTime = matcher4.group(5);
            String endMinute = matcher4.group(6);

            if (startTimeAmPm != null) {
                if (startTimeAmPm.equals("오전") || startTimeAmPm.equals("아침")) { // 오전 09시, 11시, 9시
                    resultStr.append(String.format("%02d", Integer.parseInt(startTime)));
                } else {
                    if (startTime.length() == 1) {              //ex) 오후 1시
                        resultStr.append(String.format("%02d", Integer.parseInt(startTime) + 12));
                    } else {
                        if (startTime.charAt(0) == '0') {       //ex) 오후 01시
                            resultStr.append(String.format("%02d", (Integer.parseInt(startTime) + 12)));
                        } else {                                //ex) 오후 13시
                            resultStr.append(startTime);
                        }
                    }
                }
            } else {                                            // (오전/오후)가 없을시
                if (startTime.length() == 1) {          // 1자리
                    if (Integer.parseInt(startTime) > 5) {      //ex) 9시
                        resultStr.append(String.format("%02d", Integer.parseInt(startTime)));
                    } else {                                    //ex) 1시
                        resultStr.append((Integer.parseInt(startTime) + 12));
                    }
                } else {                                // 2자리
                    if (startTime.charAt(0) == '0') {
                        if (Integer.parseInt(startTime) > 5) {  //ex) 09시
                            resultStr.append(startTime);
                        } else {                                //ex) 01시
                            resultStr.append((Integer.parseInt(startTime) + 12));
                        }
                    } else {                                    //ex) 13시
                        resultStr.append(startTime);
                    }
                }
            }
            // start 분
            if (startMinute != null) {
                resultStr.append(":").append(startMinute.replace("분", "")).append(" - ");
            } else {
                resultStr.append(":00").append(" - ");
            }

            //end 시간
            if (endTime.length() == 1) {
                resultStr.append(Integer.parseInt(endTime) + 12);
            } else {
                if (endTime.charAt(0) == '0') {
                    resultStr.append(Integer.parseInt(endTime) + 12);
                } else {
                    resultStr.append(endTime);
                }
            }
            //end 분
            if (endMinute != null) {
                resultStr.append(":").append(endMinute.replace("분", ""));
            } else {
                resultStr.append(":00");
            }

            System.out.println("timePattern4 : " + resultStr);
            return resultStr.toString();
        }

        Matcher matcher5 = timePattern5.matcher(timeCheck);            // ok
        if (matcher5.find()) {        //(오전|오후)?(\d{1,2})시(\d{2}분)?부터(접수)?
            StringBuilder resultStr = new StringBuilder();
            String startTimeAmPm = matcher5.group(1);
            String startTime = matcher5.group(2);
            String startMinute = matcher5.group(3);

            if (startTimeAmPm != null) {
                if (startTimeAmPm.equals("오전") || startTimeAmPm.equals("아침")) {
                    resultStr.append(String.format("%02d", Integer.parseInt(startTime)));
                } else {    //오후
                    if (startTime.length() == 2) {
                        if (startTime.charAt(0) == '0') {
                            resultStr.append(Integer.parseInt(startTime) + 12);
                        } else if(startTime.charAt(0) == '1'){
                            resultStr.append(startTime);
                        }
                    } else {
                        resultStr.append(Integer.parseInt(startTime) + 12);
                    }
                }
            } else {
                if (startTime.length() == 2) {
                    if (startTime.charAt(0) == '0') {
                        if (Integer.parseInt(startTime) > 5) {   //ex) 09시부터 -> 09:00 부터
                            resultStr.append(startTime);
                        } else {                        //ex) 01시30분부터 -> 13:30 부터
                            resultStr.append(Integer.parseInt(startTime) + 12);
                        }
                    } else if(startTime.charAt(0) == '1'){                            //ex) 15시30분부터 -> 15:30 부터
                        resultStr.append(startTime);
                    }
                } else {
                    if (Integer.parseInt(startTime) > 5) {
                        resultStr.append(String.format("%02d", Integer.parseInt(startTime)));
                    } else {
                        resultStr.append(Integer.parseInt(startTime) + 12);
                    }
                }
            }
            // 분
            if (startMinute != null) {
                resultStr.append(":").append(startMinute.replace("분", "")).append(" 부터");
            } else {
                resultStr.append(":00").append(" 부터");
            }

            System.out.println("timePattern5 : " + resultStr);
            return resultStr.toString();
        }

        Matcher matcher6 = timePattern6.matcher(timeCheck);             // ok
        if (matcher6.find()) {
            StringBuilder resultStr = new StringBuilder();
            String startTimeAmPm = matcher6.group(1);
            String startTime = matcher6.group(2);
            String startMinute = matcher6.group(3);

            if (startTimeAmPm != null) {
                if (startTimeAmPm.equals("오전") || startTimeAmPm.equals("아침")) {
                    resultStr.append(String.format("%02d", Integer.parseInt(startTime)));
                } else {    //오후
                    if (startTime.length() == 2) {
                        if (startTime.charAt(0) == '0') {
                            resultStr.append(Integer.parseInt(startTime) + 12);
                        } else {
                            resultStr.append(startTime);
                        }
                    } else {
                        resultStr.append(Integer.parseInt(startTime) + 12);
                    }
                }
            } else {
                if (startTime.length() == 2) {
                    if (startTime.charAt(0) == '0') {
                        if (Integer.parseInt(startTime) > 5) {   // ex) 09:00부터
                            resultStr.append(startTime);
                        } else {                                // ex) 01:00부터
                            resultStr.append(Integer.parseInt(startTime) + 12);
                        }
                    } else if(startTime.charAt(0) == '1'){                                    // ex) 11:00부터
                        resultStr.append(startTime);
                    }
                } else {
                    if (Integer.parseInt(startTime) > 5) {       // ex) 9:00부터
                        resultStr.append(String.format("%02d", Integer.parseInt(startTime)));
                    } else {                                    // ex) 1:00부터
                        resultStr.append(Integer.parseInt(startTime) + 12);
                    }
                }
            }
            resultStr.append(":").append(startMinute).append(" 부터");

            System.out.println("timePattern6 : " + resultStr);
            return resultStr.toString();
        }

        return time;
    }
}
